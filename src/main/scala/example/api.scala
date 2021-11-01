package example


import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.PrintWriter;





object GetUrlContent extends App {

    val path = "hdfs://sandbox-hdp.hortonworks.com:8020/user/maria_dev/"

        

        simpleApi(path)
        // var data = getRestContent(
        //     "https://reqres.in/api/users"
        // )
        // println(data)

        //var data = getRestContent("https://gorest.co.in/public/v1/posts")
        //var data = getRestContent("https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=spreads&dateFormat=iso&oddsFormat=american" )
        //createFile(data) 
    


    def simpleApi(path: String): String = {
        //hockey link 1 (spread)
        //https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=spreads&dateFormat=iso&oddsFormat=american        
        
        //hockey link 2 (h2h)
        //https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=h2h&dateFormat=iso&oddsFormat=american

        //hockey link 3 (over)
        //https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=totals&dateFormat=iso&oddsFormat=american

        //val url = "http://api.hostip.info/get_json.php?ip=12.215.42.19"
        val url = "https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=spreads&dateFormat=iso&oddsFormat=american"        

        //val result = scala.io.Source.fromURL(url).mkString
        val result = getRestContent(url)
        val data: ujson.Value = ujson.read(result)
        //val myArray = data.arr
        //println(data)
        data
        ///createFile(path, jsonString)
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

    def createFile(path:String, result: String): Unit = {
        val filename = path + "data.txt"
        //println(s"Creating file $filename ...")
    
        val conf = new Configuration()
        val fs = FileSystem.get(conf)
    
        // Check if file exists. If yes, delete it.
        println("Checking if it already exists...")
        val filepath = new Path(filename)
        val isExisting = fs.exists(filepath)
        if(isExisting) {
            println("Yes it does exist. Deleting it...")
            fs.delete(filepath, false)
        }

        val output = fs.create(filepath)
    
        val writer = new PrintWriter(output)
        writer.write(result)
        writer.close()
    
        println(s"Done creating file $filename ...")
        copytoLocal(path)
    } 

    def copytoLocal(path: String): Unit = {
        val target = "file:///home/maria_dev/data.txt"
        val src = path + "data.txt"
        println(s"Copying local file $src to $target ...")
    
        val conf = new Configuration()
        val fs = FileSystem.get(conf)

        val localpath = new Path(src)
        val hdfspath = new Path(target)
    
        //fs.copyFromLocalFile(false, localpath, hdfspath)
        fs.copyToLocalFile(false, localpath, hdfspath)
        println(s"Done copying local file $src to $target ...")
    }
}