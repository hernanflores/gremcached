class GremcachedGrailsPlugin {
	// the plugin version
	def version = "0.1"
	// the version or versions of Grails the plugin is designed for
	def grailsVersion = "1.3.5 > *"
	// the other plugins this plugin depends on
	def dependsOn = [:]
	// resources that are excluded from plugin packaging
	def pluginExcludes = [
		'web-app/**/*',
		'grails-app/controllers/**/*',
		'grails-app/domain/**/*',
		'grails-app/i18n/**/*',
		'grails-app/taglib/**/*',
		'grails-app/utils/**/*',
		'target/**/*',
		'test/**/*',
		'scripts/**/*',
		"grails-app/conf/CrowdinConf.groovy"
	]

	def author = "Hernán Flores Leyes"
	def authorEmail = "hernanfloresleyes@gmail.com"
	def title = "Memcached Client Plugin for Grails."
	def description = '''
Gremcached injects memcached methods to all the services/controllers of
your grails application.
'''

	// URL to the plugin's documentation
	def documentation = "http://grails.org/plugin/gremcached"

	def doWithWebDescriptor = { xml ->
		// TODO Implement additions to web.xml (optional), this event occurs before
	}

	def doWithSpring = {
		beans = {
			memcachedClient(net.rubyeye.xmemcached.utils.XMemcachedClientFactoryBean){ bean ->
				bean.destroyMethod = "shutdown"
				servers = ConfigurationHolder.config.memcached_servers
			}
		}
	}

	def doWithDynamicMethods = { applicationContext ->
		application.controllerClasses.each { controllerClass ->
			/*	Memcached convenience methods	*/
			/*	Store in cache	-	key:String, expireTime:int, objectToStore:Object(whatever)*/
			controllerClass.metaClass.store = {key, expireTime, objectToStore ->
				memcachedService.set(key, expireTime, objectToStore)
			}
			/*	Get from cache	-	key:String	*/
			controllerClass.metaClass.fetch = { key ->
				return memcachedService.get(key)
			}
			/*	remove from cache	*/
			controllerClass.metaClass.flush = { key ->
				if(memcached.get(key)){
					memcachedService.delete(key)
				}
			}
		}

		application.serviceClasses.each { serviceClass ->
			serviceClass.metaClass.store = {key, expireTime, objectToStore ->
				memcachedService.set(key, expireTime, objectToStore)
			}
			/*	Get from cache	-	key:String	*/
			serviceClass.metaClass.fetch = { key ->
				return memcachedService.get(key)
			}
			/*	remove from cache	*/
			serviceClass.metaClass.flush = { key ->
				if(memcached.get(key)){
					memcachedService.delete(key)
				}
			}
		}
	}

	def doWithApplicationContext = { applicationContext ->
		// TODO Implement post initialization spring config (optional)
	}

	def onChange = { event ->
		// TODO Implement code that is executed when any artefact that this plugin is
		// watching is modified and reloaded. The event contains: event.source,
		// event.application, event.manager, event.ctx, and event.plugin.
	}

	def onConfigChange = { event ->
		// TODO Implement code that is executed when the project configuration changes.
		// The event is the same as for 'onChange'.
	}
}
