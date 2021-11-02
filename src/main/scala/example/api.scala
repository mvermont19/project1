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

    
    

    def simpleApi() {
        //hockey link 1 (spread)
        //https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=spreads&dateFormat=iso&oddsFormat=american        
        
        //hockey link 2 (h2h)
        //https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=h2h&dateFormat=iso&oddsFormat=american

        //hockey link 3 (over)
        //https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=totals&dateFormat=iso&oddsFormat=american

        val url = "https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=spreads&dateFormat=iso&oddsFormat=american"        
        val url2 = "https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=h2h&dateFormat=iso&oddsFormat=american"
        val url3 = "https://api.the-odds-api.com/v4/sports/icehockey_nhl/odds?apiKey=a6d9bb29231ea761de5367730a1a8961&regions=us&markets=totals&dateFormat=iso&oddsFormat=american"
        val result = getRestContent(url)
        val result2 = getRestContent(url2)
        val result3 = getRestContent(url3)

        createFile(result, 1)
        createFile(result2, 2)
        createFile(result3, 3)
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

    def createFile(result: String, file: Int): Unit = {
        val path = "hdfs://sandbox-hdp.hortonworks.com:8020/user/maria_dev/"
        var filename = ""
        file match{
            case 1 => filename = path + "spreads.txt"
            case 2 => filename = path + "h2h.txt"
            case 3 => filename = path + "totals.txt"
        }
        println(filename)
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
        copytoLocal(file)
    } 

    def copytoLocal(file: Int): Unit = {
        val path = "hdfs://sandbox-hdp.hortonworks.com:8020/user/maria_dev/"
        var target = ""
         file match{
            case 1 => target = "file:///home/maria_dev/spreads.txt"
            case 2 => target = "file:///home/maria_dev/h2h.txt"
            case 3 => target =  "file:///home/maria_dev/totals.txt"
        }
        var src = ""
        file match{
            case 1 => src = path + "spreads.txt"
            case 2 => src = path + "h2h.txt"
            case 3 => src = path + "totals.txt"
        }
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