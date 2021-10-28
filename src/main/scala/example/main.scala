package example

import example._

import scala.io.StdIn

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

object Main{

    def main(args: Array[String]){
 

    }

    def connectToHive() {
        var con: java.sql.Connection = null;
        try {
            // For Hive2:
            var driverName = "org.apache.hive.jdbc.HiveDriver"
            val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";

            Class.forName(driverName);

            con = DriverManager.getConnection(conStr, "", "");
            val stmt = con.createStatement();
            stmt.executeQuery("Show databases");
            System.out.println("show database successfully");


        } catch {
            case ex => {
                ex.printStackTrace();
                throw new Exception(s"${ex.getMessage}")
            }
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch {
                case ex => {
                    ex.printStackTrace();
                    throw new Exception(s"${ex.getMessage}")
                }
            }
        }
    }

    def userInterface(){
        var answer: Int = 0
        var exit: Boolean = false

        //login()

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
                            showAll(original)
                        }
                        case 2 => {
                            //search
                            searchTeam(original)
                        }
                        case 3 => {
                            //Biggest (favorite)
                            find(original, true)
                        }
                        case 4 => {
                            //Smallest (underdog)
                            find(original, false)
                        }
                        case _ => {
                            println("Not a vaild choice please try again")
                        }
                    }
                }
                case 4 => {
                    //user choices
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

    def showAll(choice: Int): Unit = {
        
    }

    def searchTeam(choice: Int): Unit = {
        print("Which team would you like to find: ")
        val team: String = StdIn.readLine()

    }

    def find(choice: Int, findFav: Boolean): Unit = {

    }
}