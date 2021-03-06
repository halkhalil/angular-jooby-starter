/**
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */
package de.protubero.ajs;

import org.jooby.Jooby;
import org.jooby.apitool.ApiTool;
import org.jooby.banner.Banner;
import org.jooby.json.Jackson;
import org.jooby.metrics.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.typesafe.config.Config;

import io.vavr.control.Try;

/**
 * 
 * 
 * @author MSchaefer
 *
 */
public class App extends Jooby {

	final Logger logger = LoggerFactory.getLogger(getClass());

	static String TITLE = "Angular Jooby Starter";

	{
		// display a banner on the console when the server starts up
		use(new Banner(TITLE).font("slant"));

		
		// Include Jackson, providing the JSON parser and renderer
		use(new Jackson());

		// make the person stare available as singleton via dependency injection
		bind(new PersonStore());

		// Init person store at startup with data loaded from the configuration
		onStart(registry -> {
			Config config = require(Config.class);
			PersonStore store = require(PersonStore.class);
			
			config.getConfig("sampledata").getObjectList("data").forEach(c -> {
				Config personConfig = c.toConfig();
				Person person = new Person();
				person.setName(personConfig.getString("name"));
				person.setAge(personConfig.getInt("age"));
				person.setEmail(personConfig.getString("email"));
				store.insert(person);

				logger.info("Saving {}", person);
			});

		});
		
		// provide swagger API, but only for the API that is used by the client
	   use(new ApiTool()
			     .swagger("/swagger")
			     .raml("/raml")
			     .filter(route -> route.pattern().startsWith("/api/"))
			   );

		
		// provide health checks services
		use(new Metrics().request().threadDump().ping().healthCheck("db", new HealthCheck() {

			@Override
			protected Result check() throws Exception {
				Try<Void> tTry = Try.run(() -> {
					// Here you should do a quick test, if the app is basically healthy, i.e. check the db connection  
				});
				if (tTry.isFailure()) {
					return HealthCheck.Result.unhealthy(tTry.getCause());
				} else {
					return HealthCheck.Result.healthy("OK");
				}
			}

		}).metric("memory", new MemoryUsageGaugeSet()).metric("threads", new ThreadStatesGaugeSet())
				.metric("gc", new GarbageCollectorMetricSet()).metric("fs", new FileDescriptorRatioGauge()));

		// Provide initialization data to the angular client
		get("/api/init", (req, rsp) -> {
			Config config = require(Config.class);
			rsp.send(new ClientInit(config.getString("title")));
		});

		// Provides the API which is used by the angular client
		use(PersonController.class);

		// Include a jooby module to serve the static files that make up the client
		use(new AngularClient());
	}

	public static void main(final String[] args) {
		// start the server
		run(App::new, args);
	}

}
