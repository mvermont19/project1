package example

import example._

import scala.io.StdIn

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

import net.liftweb.json._

import scala.collection.immutable._

object Main{

    def main(args: Array[String]){
        //GetUrlContent
        //var con = connectToHive()
        val t = new Tuple2("admin", true)
        var users = Map("admin" -> new Tuple2("admin", true), "user" -> new Tuple2("user", false))
        //userInterface(con, users)
    }

    def connectToHive(): Connection = {
        var con: java.sql.Connection = null;
        try {
            // For Hive2:
            var driverName = "org.apache.hive.jdbc.HiveDriver"
            val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";

            Class.forName(driverName);

            con = DriverManager.getConnection(conStr, "", "");
            
            //create db
            //stmt.executeQuery("Show databases");
            //System.out.println("show database successfully");

            //create table
            val tableName = "odds";
            println(s"Dropping table $tableName..")
            val stmt = con.createStatement();
            stmt.execute("drop table IF EXISTS " + tableName);
            println(s"Creating table $tableName..")
            stmt.execute(
                "create table " + tableName + " (key int, value string) row format delimited  fields terminated by ','"
            );

            //insert data

        } catch {
            case ex: Throwable => {
                ex.printStackTrace();
                throw new Exception(s"${ex.getMessage}")
            }
        } finally {
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
        con
    }

    def userInterface(con: Connection, users: Map[String,(String, Boolean)]){
        var answer: Int = 0
        var exit: Boolean = false

        var user = login(users)

        while(!exit){
            println("")
            Thread.sleep(1000)

            val intro: String = "Please choose an option to learn about the Betting Lines: "
            val startingScreen: String = "1. Head to Head (moneyline) \n2. Spread \n3. Totals \n4. User Options \n5. Exit"

            val h2hInfo: String = "What would you like to know about the Head to Head bets: "
            val spreadInfo : String = "What would you like to know about the Spread bets: "
            val choices: String = "1. Show all \n2. Search for a team \n3. Biggest favorite \n4. Biggest Underdog"
            
            val totalsInfo: String = "What would you like to know about the totals for a game: "
            val totalsChoices: String = "1. Show all \n2. Search for a team \n3. Biggest over \n4. Smallest over"

            val changesInfo: String = "What changes would you like to make to your account: "
            val changesBasicChoices: String = "1. Change username \n2. Change password"
            val changesAdminChoices: String = "1. Change username \n2. Change password \n3. Something else (add user?) "

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
                    var isAdmin = checkAdmin(user, users)
                    if(!isAdmin){
                        answer = getUserInput(changesInfo, changesBasicChoices)
                        answer match {
                            case 1 => {

                            }
                            case 2 => {

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

                            }
                            case 2 => {

                            }
                            case 3 => {

                            }
                            case 4 => {

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
        println(userInput)
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
    def login(users: Map[String,(String, Boolean)]): String = {
        var username = ""
        var goodLogin = false
        while(!goodLogin){
            print("Please enter your username: ")
            username = StdIn.readLine()
            println(username)
            if(users.keySet.contains(username)){
                print("Please enter your password: ")
                var password = StdIn.readLine()
                println(password)
                if(users.get(username).equals(password)){ //this is wrong
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
        var sql = "select * from odds"
        choice match {
            case 1 => {
                sql += " where markets = h2h"
            }
            case 2 => {
                sql += " where markets = spreads"
            }
            case 3 => {
                sql += " where markets = totals"
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
        choice match{
            case 1 => {
                //team h2h

            }
            case 2 => {
                //team spread

            }
            case 3 => {
                //team over

            }
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

    def checkAdmin(user: String, users: Map[String, (String, Boolean)]): Boolean = {
        var isAdmin = false
        if(users.get(user) == true){ //this is wrong
            isAdmin = true
        }
        isAdmin
    }   
}