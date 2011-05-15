package gremcached

import net.rubyeye.xmemcached.MemcachedClient
import net.rubyeye.xmemcached.MemcachedClientBuilder
import net.rubyeye.xmemcached.XMemcachedClientBuilder
import net.rubyeye.xmemcached.exception.*
import net.rubyeye.xmemcached.utils.*;

class MemcachedService {

	static transactional = true

	def memcachedClient


	def set(String key, int expireTime, def objectToStore){
		try {
			log.info("Storing object: ${objectToStore} with key: ${key}. Caching expires in ${expireTime}")
			memcachedClient.set(key, expireTime, objectToStore)
		} catch (MemcachedException e) {
			log.error("MemcachedClient operation fail")
			log.error(e.getMessage())
		} catch (InterruptedException e) {
			// ignore
		}
	}

	def get(def key){
		try {
			log.debug("Looking for object ${key}")
			def value = memcachedClient.get(key)
			if(value){
				log.info("Got it!. Value: ${value}")
			}
			else{
				log.info("Cache miss for key: ${key}")
			}
			return value //client MUST validate object status
		} catch (MemcachedException e) {
			log.error("MemcachedClient operation fail");
			log.error(e.getMessage())
		}  catch (InterruptedException e) {
			// ignore
		}
	}

	def delete(def key){
		try {
			log.debug("Deleting object with key: ${key} from cache")
			//Must fetch before to avoid stackOverflowException
			if(memcachedClient.get(key)){
				memcachedClient.delete(key)
			}
		}
		catch (MemcachedException e) {
			log.error("MemcachedClient operation fail");
			log.error(e.getMessage())
		}
	}
}
