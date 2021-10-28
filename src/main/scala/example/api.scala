package example

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

object GetUrlContent extends App {
    simpleApi()
    var data = getRestContent(
        "https://reqres.in/api/users"
    )
    println(data)

    data = getRestContent("https://gorest.co.in/public/v1/posts")
    println(data) 

    def simpleApi(): Unit = {
        val url = "http://api.hostip.info/get_json.php?ip=12.215.42.19"
        val result = scala.io.Source.fromURL(url).mkString
        println(result)
    }

    /** Returns the text content from a REST URL. Returns a blank String if there
    * is a problem. (Probably should use Option/Some/None; I didn't know about
    * it back then.)
    */
    def getRestContent(url: String): String = {
        val httpClient = new DefaultHttpClient()
        val httpResponse = httpClient.execute(new HttpGet(url))
        val entity = httpResponse.getEntity()
        var content = ""
        if (entity != null) {
            val inputStream = entity.getContent()
            content = scala.io.Source.fromInputStream(inputStream).getLines.mkString
            inputStream.close
        }
        httpClient.getConnectionManager().shutdown()
        return content
    }
}