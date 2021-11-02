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


    def main(args: Array[String]){
        GetUrlContent.simpleApi()

        //val data = result.toJson()

        //val myArray = data.arr
        //println(data)
        //implicit val formats = DefaultFormats
        //val jsonString = write(result)

        //GetUrlContent.createFile(path, result)
        var con = connectToHive()
        //val t = new Tuple2("admin", true)
        
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

            //create table
//             val tableName = "h2h";
//             //println(s"Dropping table $tableName..")
//             stmt.execute("drop table IF EXISTS " + tableName);
//             println(s"Creating table $tableName..")
//             stmt.execute(
//                 s"create external table ${tableName} (id string, sport_key string, sport_title string, commence_time string, home_team string, away_team string, bookmakers struct<key: string, title: string, last_update: string, markets: struct<key: string, outcomes: struct<name: string, price: int, point: int>>>)
// row format serde 'org.openx.data.jsonserde.JsonSerDe'
// stored as textfile;"
//             );
            //res = stmt.executeQuery("add jar hdfs:///user/maria_dev/.hiveJars/json-udf-1.3.8-jar-with-dependencies.jar;")
            //res = stmt.executeQuery("add jar hdfs:///user/maria_dev/.hiveJars/json-serde-1.3.8-jar-with-dependencies.jar;")


            res = stmt.executeQuery("LOAD DATA LOCAL INPATH 'file:///home/maria_dev/spreads.txt' OVERWRITE INTO TABLE spreads")
            res = stmt.executeQuery("LOAD DATA LOCAL INPATH 'file:///home/maria_dev/h2h.txt' OVERWRITE INTO TABLE h2h")
            res = stmt.executeQuery("LOAD DATA LOCAL INPATH 'file:///home/maria_dev/totals.txt' OVERWRITE INTO TABLE totals")

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
      * This is what the user wil interact with
      * @param con
      */
    def userInterface(con: Connection){
        var answer: Int = 0
        var exit: Boolean = false

        var user = login()

        while(!exit){
            println("")
            Thread.sleep(1000)

            val intro: String = s"${user}, Please choose an option to learn about the Betting Lines: "
            val startingScreen: String = "1. Head to Head (moneyline) \n2. Spread \n3. Totals \n4. User Options \n5. Exit"

            val h2hInfo: String = "What would you like to know about the Head to Head bets: "
            val spreadInfo : String = "What would you like to know about the Spread bets: "
            val choices: String = "1. Show all \n2. Search for a team \n3. Biggest favorite \n4. Biggest Underdog"
            
            val totalsInfo: String = "What would you like to know about the totals for a game: "
            val totalsChoices: String = "1. Show all \n2. Search for a team \n3. Biggest over \n4. Smallest over"

            val changesInfo: String = "What changes would you like to make to your account: "
            val changesBasicChoices: String = "1. Change username \n2. Change password"
            val changesAdminChoices: String = "1. Change username \n2. Change password \n3. Add User \n4. Show all users "

            answer = getUserInput(intro, startingScreen)
            answer match {
                case 1 | 2 | 3 => {
                    //h2h and spread and totals
                    var original = answer
                    if(answer == 1){
                        //h2h
                        answer = getUserInput(h2hInfo, choices)
                    }
                    else if(answer == 2) {
                        //spread
                        answer = getUserInput(spreadInfo, choices)
                    }
                    else{
                        //totals
                        answer = getUserInput(totalsInfo, totalsChoices)
                    }
                    answer match {
                        case 1 => {
                            //all
                            showAll(con, original)
                        }
                        case 2 => {
                            //search
                            searchTeam(con, original)
                        }
                        case 3 => {
                            //Biggest (favorite)
                            find(con, original, true)
                        }
                        case 4 => {
                            //Smallest (underdog)
                            find(con, original, false)
                        }
                        case _ => {
                            println("Not a vaild choice please try again")
                        }
                    }
                }
                case 4 => {
                    //user choices
                    var isAdmin = checkAdmin(user)
                    if(!isAdmin){
                        answer = getUserInput(changesInfo, changesBasicChoices)
                        answer match {
                            case 1 => {
                                user = changeUserName(user)
                            }
                            case 2 => {
                                changePassword(user)
                            }
                            case _ => {
                                println("Not a vaild choice please try again")
                            }
                        }
                    }
                    else{
                        answer = getUserInput(changesInfo, changesAdminChoices)
                        answer match {
                            case 1 => {
                                user = changeUserName(user)
                            }
                            case 2 => {
                                changePassword(user)
                            }
                            case 3 => {
                                addUser()
                            }
                            case 4 => {
                                showUsers()
                            }
                            case _ => {
                                println("Not a vaild choice please try again")
                            }
                        }
                    }
                }
                case 5 => {
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
            }
        
        }
    }

    /**
      * This helper method gets the user input while checking to make
      * sure it is a valid choice and returns their answer
      *
      * @return
      */
    def getUserInput(strings: String*): Int = {
      var validAnswer: Boolean = false
      var userInput: String = ""
      var answer = 0

      while(!validAnswer){
        println(strings(0))
        println(strings(1))
        userInput = StdIn.readLine()
        if(userInput.isEmpty())  {
          println("Please give an answer")
        }
        else {
          var num: Char = userInput.charAt(0)
          if(num.isDigit && (userInput.toInt == 1 || userInput.toInt == 2
                              || userInput.toInt == 3 || userInput.toInt == 4  || userInput.toInt == 5)){
            validAnswer = true
            answer = userInput.toInt
          }
          else{
            println("Not a valid answer. Try again")
          }
        }
      }
      answer
    }

    /**
      * This function is checking to make sure the user has a valid login and password
      */
    def login(): String = {
        var username = ""
        var goodLogin = false
        while(!goodLogin){
            print("Please enter your username: ")
            username = StdIn.readLine()
            if(users.keySet.contains(username)){
                print("Please enter your password: ")
                var password = StdIn.readLine()
                if(users.get(username).get._1.equals(password)){ 
                    println("Welcome " + username)
                    goodLogin = true
                }
                else{
                    println("Password incorrect. Please try again")
                }
            }
            else{
                println("Username not found. Please try again")
            }
        }
        username
    }

    /**
      * This function prints out all the lines saved in the database from each book
      *
      * @param con
      * @param choice
      */
    def showAll(con: Connection, choice: Int): Unit = {
        var sql = ""
        choice match {
            case 1 => {
                sql += "select * from h2h"
            }
            case 2 => {
                sql += "select * from spreads"
            }
            case 3 => {
                sql += "select * from totals"
            }
        }
        
        System.out.println("Running: " + sql);
        val stmt = con.createStatement();
        val res = stmt.executeQuery(sql);
        while (res.next()) {
            System.out.println(
                String.valueOf(res.getInt(1))
            );
        }
    }

    /**
      * This prints out the lines for a specific team from each book
      *
      * @param con
      * @param choice
      */
    def searchTeam(con: Connection, choice: Int): Unit = {
        print("Which team would you like to find: ")
        val team: String = StdIn.readLine()
        var sql = ""
        choice match{
            case 1 => {
                //team h2h
                sql += s"select * from h2h where home_team = ${team} or away_team = ${team}"
            }
            case 2 => {
                //team spread
                sql += s" select * from spreads home_team = ${team} or away_team = ${team}"

            }
            case 3 => {
                //team over
                sql += s"select * from totals home_team = ${team} or away_team = ${team}"

            }
        }
        
        System.out.println("Running: " + sql);
        val stmt = con.createStatement();
        val res = stmt.executeQuery(sql);
        while (res.next()) {
            System.out.println(
                String.valueOf(res.getInt(1))
            );
        }
    }

    /**
      * This prints out the biggest favorite (lowest neg number) or biggest underdog(biggest pos number)
      * or the biggest over based off the choices
      *
      * @param con
      * @param choice
      * @param findFav
      */
    def find(con: Connection, choice: Int, findFav: Boolean): Unit = {
        choice match {
            case 1 => {
                var num: Int = 0
                if(findFav){
                    //h2h biggest favorite

                }
                else{
                    //h2h biggest underdog

                }
            }
            case 2 => {
                if(findFav){
                    //spread biggest favorite

                }
                else{
                    //spread biggest underdog

                }
            }
            case 3 => {
                if(findFav){
                    //biggest over

                }
                else{
                    //smallest over

                }

            }
        }
    }

    /**
      * Returns if the user is an admin or not
      *
      * @param user
      * @return
      */
    def checkAdmin(user: String): Boolean = {
        var isAdmin = false
        if(users.get(user).get._2 == true){ 
            isAdmin = true
        }
        isAdmin
    }   

    /**
      * This changes the username of the current user and returns it
      */
    def changeUserName(user: String): String ={
        var test = login() //gotta change
        var newUser = ""
        if(test.equals(user)){
            print("What would you like to change your username to: ")
            newUser = StdIn.readLine()
            if(!users.keySet.contains(newUser)){
                val pass = users.get(user).get
                users = users.-(user)
                val t = (newUser, pass)
                users += t
                println(s"User name is now: ${newUser}")
            }
        }
        newUser
    }

    /**
      * This changes the password of the current user
      */
    def changePassword(user: String){
        var test = login() //gotta change
        if(test.equals(user)){
            print("What would you like to change your password to (Between 8-16 characters): ")
            val newPass = StdIn.readLine()
            if(checkPassword(newPass)){
                print("Please retype password: ")
                val pass2 = StdIn.readLine()
                if(checkPassword(pass2) && newPass.equals(pass2)){
                    val t = (user, (newPass, users.get(user).get._2))
                    users += t
                    println("Password has been updated")
                }
            }    
        }
    }

    /**
      * This adds another user to the list of eligible users
      */
    def addUser(){
        var added: Boolean = false
        while (!added){
            print("Type in a username: ")
            val newUser = StdIn.readLine()
            if(!users.contains(newUser)){
                print("Please give a password (Between 8-16 characters): ")
                val pass1 = StdIn.readLine()
                if(checkPassword(pass1)){
                    print("Please retype password: ")
                    val pass2 = StdIn.readLine()
                    if(checkPassword(pass2) && pass1.equals(pass2)){
                        print("Give this user admin priviladges (Type 'y' or 'n'): ")
                        val admin = StdIn.readLine()
                        admin match {
                            case "y" => {
                                val t = (newUser, (pass1, true))
                                users += t
                                added = true
                            }
                            case "n" => {
                                val t = (newUser, (pass1, false))
                                users += t
                                added = true
                            }
                            case _ => {
                                println("Not a valid response")
                            }
                        }
                    }
                    else{
                        println("Passwords do not match, try again")
                    }
                }
                else{
                    println("Password does not meet specs, try again")
                }
            }
            else{
                println("Username already taken, try again")
            }

        }
    }

    /**
      * This is a helper method to check and make sure the password meets the specs
      */
    def checkPassword(pass: String): Boolean = {
        pass.length() >= 8 && pass.length() <= 16
    }

    /**
      * Displays all the users
      */
    def showUsers() {
        for(k <- users.keySet){
            println("Username \t Password \t Is Admin")
            println(s"${k} \t\t ${users.get(k).get._1} \t\t ${users.get(k).get._2}")
        }
    }
}