package config

import com.typesafe.config.ConfigFactory

object ConfigLoader {
  private val conf = ConfigFactory.load()

  object Yelp {
    val CONSUMER_KEY = conf.getString("consumer_key")
    val CONSUMER_SECRET = conf.getString("consumer_secret")
    val TOKEN = conf.getString("token")
    val TOKEN_SECRET = conf.getString("token_secret")
  }

  object Google {
    val SERVER_API_KEY = conf.getString("server_api_key")
  }
}
