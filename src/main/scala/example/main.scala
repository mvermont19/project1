package example

import example._

import scala.io.StdIn

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

import net.liftweb.json._
import net.liftweb.json.Serialization.write

import scala.collection.immutable._

object project{

    var users = Map("admin" -> new Tuple2("admin", true), "user" -> new Tuple2("user", false))
    val path = "hdfs://sandbox-hdp.hortonworks.com:8020/user/maria_dev/"

    case class Line(name: String, price: Int, point: Int)
    case class Book(key: String, outcome: Array[Line])    
    case class Maker(key: String, title: String, updated: String, markets: Book)
    case class Game(id: String, sport_key: String, sport_title: String, commence_time: String, home_team: String, away_team: String, bookmakers: Array[Maker])

    def main(args: Array[String]){
        GetUrlContent.simpleApi()
        var con = connectToHive()        
        userInterface(con)
    }

    def connectToHive(): Connection = {
        var con: java.sql.Connection = null;
        try {
            // For Hive2:
            var driverName = "org.apache.hive.jdbc.HiveDriver"
            val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/bets";

            Class.forName(driverName);

            con = DriverManager.getConnection(conStr, "", "");
            
            //create db
            val stmt = con.createStatement()
            //var res = stmt.executeQuery("create database if not exists bets")
            var res = stmt.executeQuery("Show tables");
            if (res.next()) {
                System.out.println(res.getString(1));
            }
            //System.out.println("show database successfully");
            //res = stmt.executeQuery("use bets")

            //stmt.execute("add jar hdfs:///user/maria_dev/.hiveJars/json-udf-1.3.8-jar-with-dependencies.jar;")
            //stmt.execute("add jar hdfs:///user/maria_dev/.hiveJars/json-serde-1.3.8-jar-with-dependencies.jar;")

            //create table
            val tableName = Array("h2h", "spreads", "totals");
            //println(s"Dropping table $tableName..")

            // stmt.execute("drop table IF EXISTS " + tableName(0)); 
            // stmt.execute("drop table IF EXISTS " + tableName(1)); 
            // stmt.execute("drop table IF EXISTS " + tableName(2));        
            //stmt.execute("drop table IF EXISTS " + tableName);
            println(s"Creating tables..")
//             stmt.execute(
//                 s"create external table ${tableName} (id string, sport_key string, sport_title string, commence_time string, home_team string, away_team string, bookmakers struct<key: string, title: string, last_update: string, markets: struct<key: string, outcomes: struct<name: string, price: int, point: int>>>)
// row format serde 'org.openx.data.jsonserde.JsonSerDe'
// stored as textfile;"
//             );
            for(t <- tableName){
                stmt.execute("drop table IF EXISTS " + t);
                stmt.execute(s"create table $t (json string)")
            }

            stmt.execute("LOAD DATA INPATH 'hdfs://sandbox-hdp.hortonworks.com:8020/user/maria_dev/spreads.json' OVERWRITE INTO TABLE spreads")
            stmt.execute("LOAD DATA INPATH 'hdfs://sandbox-hdp.hortonworks.com:8020/user/maria_dev/h2h.json' OVERWRITE INTO TABLE h2h")
            stmt.execute("LOAD DATA INPATH 'hdfs://sandbox-hdp.hortonworks.com:8020/user/maria_dev/totals.json' OVERWRITE INTO TABLE totals")

        } catch {
            case ex: Throwable => {
                ex.printStackTrace();
                throw new Exception(s"${ex.getMessage}")
            }
        } finally {
            // try {
            //     if (con != null)
            //         con.close();
            // } catch {
            //     case ex: Throwable => {
            //         ex.printStackTrace();
            //         throw new Exception(s"${ex.getMessage}")
            //     }
            // }
        }
        con
    }

    /**
      * This is what the user will interact with
      * @param con
      */
    def userInterface(con: Connection){
        var answer: Int = 0
        var exit: Boolean = false

        while(!exit){
            //println("")
            //Thread.sleep(1000)
            
            val loginChoices: String = "1. Create User \n2. Login \n3. Exit"

            answer = User.getUserInput("", loginChoices)
            answer match {
                case 1 => {
                    User.addUser()
                }
                case 2 => {
                    var user = User.login()
                    var logout: Boolean = false
                    println("Welcome " + user)
                    while(!logout){
                        println("")
                        val intro: String = s"${user}, Please choose an option to learn about the Betting Lines: "
                        val startingScreen: String = "1. Head to Head (moneyline) \n2. Spread \n3. Totals \n4. User Options \n5. Logout"

                        val h2hInfo: String = "What would you like to know about the Head to Head bets: "
                        val spreadInfo : String = "What would you like to know about the Spread bets: "
                        val choices: String = "1. Show all \n2. Search for a team \n3. Biggest Home favorite \n4. Biggest Home Underdog"
                        
                        val totalsInfo: String = "What would you like to know about the totals for a game: "
                        val totalsChoices: String = "1. Show all \n2. Search for a team \n3. Biggest over \n4. Smallest over"

                        val changesInfo: String = "What changes would you like to make to your account: "
                        val changesBasicChoices: String = "1. Change username \n2. Change password"
                        val changesAdminChoices: String = "1. Change username \n2. Change password \n3. Show all users "

                        answer = User.getUserInput(intro, startingScreen)
                        answer match {
                            case 1 | 2 | 3 => {
                                //h2h and spread and totals
                                var original = answer
                                var firstChoiceMade = true
                                while(firstChoiceMade){
                                    if(original == 1){
                                        //h2h
                                        answer = User.getUserInput(h2hInfo, choices)
                                    }
                                    else if(original == 2) {
                                        //spread
                                        answer = User.getUserInput(spreadInfo, choices)
                                    }
                                    else{
                                        //totals
                                        answer = User.getUserInput(totalsInfo, totalsChoices)
                                    }
                                    answer match {
                                        case 1 => {
                                            //all
                                            Quiries.showAll(con, original)
                                            firstChoiceMade = false
                                        }
                                        case 2 => {
                                            //search
                                            Quiries.searchTeam(con, original)
                                            firstChoiceMade = false

                                        }
                                        case 3 => {
                                            //Biggest (favorite)
                                            Quiries.find(con, original, true)
                                            firstChoiceMade = false

                                        }
                                        case 4 => {
                                            //Smallest (underdog)
                                            Quiries.find(con, original, false)
                                            firstChoiceMade = false
                                        }
                                        case _ => {
                                            println("Not a vaild choice please try again")
                                        }
                                    }
                                }
                            }
                            case 4 => {
                                //user choices
                                var firstChoiceMade = true
                                while(firstChoiceMade){
                                    var isAdmin = User.checkAdmin(user)
                                    if(!isAdmin){
                                        answer = User.getUserInput(changesInfo, changesBasicChoices)
                                        answer match {
                                            case 1 => {
                                                user = User.changeUserName(user)
                                                firstChoiceMade = false
                                            }
                                            case 2 => {
                                                User.changePassword(user)
                                                firstChoiceMade = false
                                            }
                                            case _ => {
                                                println("Not a vaild choice please try again")
                                            }
                                        }
                                    }
                                    else{
                                        //admin choices
                                        answer = User.getUserInput(changesInfo, changesAdminChoices)
                                        answer match {
                                            case 1 => {
                                                user = User.changeUserName(user)
                                                firstChoiceMade = false
                                            }
                                            case 2 => {
                                                User.changePassword(user)
                                                firstChoiceMade = false
                                            }
                                            case 3 => {
                                                User.showUsers()
                                                firstChoiceMade = false
                                            }     
                                            case _ => {
                                                println("Not a vaild choice please try again")
                                            }
                                        }       
                                    }
                                }
                            }
                            case 5 => {
                                //logout
                                logout = true
                            }                  
                        }
                    }
                }
                case 3 => {
                    //exit
                    exit = true
                    try {
                        if (con != null)
                            con.close();
                    } catch {
                        case ex: Throwable => {
                            ex.printStackTrace();
                            throw new Exception(s"${ex.getMessage}")
                        }
                    }
                }
                case _ => {
                    println("Not a vaild choice please try again")

                }
            }
        
        }
    }
}