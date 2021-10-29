package example

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient



object GetUrlContent extends App {
    simpleApi()
    //https://api.the-odds-api.com/v4/sports/upcoming/odds/?regions=us&sport=icehockey_nhl&sport=americanfootball_nfl&sport=basketball_nba&markets=h2h&markets=spreads&markets=totals&apiKey=a6d9bb29231ea761de5367730a1a8961
    var data = getRestContent(
        "https://reqres.in/api/users"
    )
    println(data)

    data = getRestContent("https://gorest.co.in/public/v1/posts")
    println(data) 

    def simpleApi(): Unit = {
        //hockey link 1 (spread)
        //https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=spreads&dateFormat=iso&oddsFormat=american        
        
        //hockey link 2 (h2h)
        //https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=h2h&dateFormat=iso&oddsFormat=american

        //hockey link 3 (over)
        //https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=totals&dateFormat=iso&oddsFormat=american

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