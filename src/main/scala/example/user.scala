package example

import scala.io.StdIn

object User {
    var users = Map("admin" -> new Tuple2("admin", true), "user" -> new Tuple2("user", false))

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
                var goodPassword = false
                while(!goodPassword){
                    print("Please enter your password: ")
                    var password = StdIn.readLine()
                    if(users.get(username).get._1.equals(password)){ 
                        goodPassword = true
                    }
                    else{
                        println("Password incorrect. Please try again")
                    }
                }
                goodLogin = true
            }
            else{
                println("Username not found. Please try again")
            }
        }
        username
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
        var newUser = ""
        var goodUsername = false
        while(!goodUsername){
            print("Please enter your username: ")
            var username = StdIn.readLine()
            if(users.keySet.contains(username) && username.equals(user)){
                var goodPassword = false
                while(!goodPassword){
                    print("Please enter your password: ")
                    var password = StdIn.readLine()
                    if(users.get(username).get._1.equals(password)){ 
                        var goodChange = false
                        while(!goodChange){
                            print("What would you like to change your username to: ")
                            newUser = StdIn.readLine()
                            if(!users.keySet.contains(newUser)){
                                val pass = users.get(user).get
                                users = users.-(user)
                                val t = (newUser, pass)
                                users += t
                                println(s"User name is now: ${newUser}")
                                goodChange = true
                                goodPassword = true
                                goodUsername = true
                            }
                            else{
                                println("Username already exists")
                            }
                        }    
                    }
                    else{
                        println("Password incorrect. Please try again")
                    }
                }
            }
            else{
                println("Username not found. Please try again")
            }
        }
        
        newUser
    }

    /**
      * This changes the password of the current user
      */
    def changePassword(user: String){
        var goodUsername = false
        while(!goodUsername){
            print("Please enter your username: ")
            var username = StdIn.readLine()
            if(users.keySet.contains(username) && username.equals(user)){
                var goodPassword = false
                while(!goodPassword){
                    print("Please enter your password: ")
                    var password = StdIn.readLine()
                    if(users.get(username).get._1.equals(password)){ 
                        var goodChange = false
                        while(!goodChange){
                           print("What would you like to change your password to (Between 8-16 characters): ")
                            val newPass = StdIn.readLine()
                            if(checkPassword(newPass)){
                                print("Please retype password: ")
                                val pass2 = StdIn.readLine()
                                if(checkPassword(pass2) && newPass.equals(pass2)){
                                    val t = (user, (newPass, users.get(user).get._2))
                                    users += t
                                    println("Password has been updated")
                                    goodChange = true
                                    goodPassword = true
                                    goodUsername = true
                                }
                                else{
                                    println("Passwords didn't match, try again")
                                }
                            }
                            else{
                                println("Password didn't meet specs, try again")
                            }
                        }        
                    }
                    else{
                        println("Password incorrect. Please try again")
                    }
                }
            }
            else{
                println("Username not found. Please try again")
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