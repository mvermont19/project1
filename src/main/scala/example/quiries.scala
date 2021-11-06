package example

import java.sql.Connection
import scala.io.StdIn

object Quiries {
    /**
      * This function prints out all the lines saved in the database from each book
      *
      * @param con
      * @param choice
      */
    def showAll(con: Connection, choice: Int): Unit = {
        var sql = ""
        val stmt = con.createStatement();
        choice match {
            case 1 => {
                sql += "select get_json_object(json,'$.bookmakers[0].markets.outcomes[0].name')," +
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].price'), " + 
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].name')," +
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].price') from h2h"

                val res = stmt.executeQuery(sql);
                printf("%-22s%-22s%-22s%-22s\n", " Home Team", "Line", "Away Team", "Line")
                while (res.next()) {
                    if(res.getString(1) != null){
                        System.out.printf("%-22s%-22s%-22s%-22s\n",
                            String.valueOf(res.getString(1)), 
                            String.valueOf(res.getString(2)),
                            String.valueOf(res.getString(3)),
                            String.valueOf(res.getString(4))
                        );
                    }
                }
            }
            case 2 => {
                sql += "select get_json_object(json,'$.bookmakers[0].markets.outcomes[0].name')," +
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].price'), " + 
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].point')," +
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].name')," +
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].price'), " +
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].point') from spreads"

                val res = stmt.executeQuery(sql);
                printf("%-22s%-22s%-13s%-22s%-22s%-13s\n", "Home Team", "Line", "Margin", "Away Team", "Line", "Margin")
                while (res.next()) {
                    if(res.getString(1) != null){
                        System.out.printf("%-22s%-22s%-13s%-22s%-22s%-13s\n",
                            String.valueOf(res.getString(1)),
                            String.valueOf(res.getString(2)),
                            String.valueOf(res.getString(3)),
                            String.valueOf(res.getString(4)),
                            String.valueOf(res.getString(5)),
                            String.valueOf(res.getString(6))
                        );
                    }
                }
            }
            case 3 => {
                sql += "select get_json_object(json,'$.home_team')," +
                  "get_json_object(json,'$.away_team'), " + 
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].name')," +
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].price')," +
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].name'), " +
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].price'), " +
                  "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].point') from totals"

                val res = stmt.executeQuery(sql);
                printf("%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n", "Home Team", "Away Team ", "Over/Under", "Line", "Over/Under", "Line", "Total Score")
                while (res.next()) {
                    if(res.getString(3) != null){
                        System.out.printf("%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n",
                            String.valueOf(res.getString(1)), 
                            String.valueOf(res.getString(2)),
                            String.valueOf(res.getString(3)),
                            String.valueOf(res.getString(4)),
                            String.valueOf(res.getString(5)),
                            String.valueOf(res.getString(6)),
                            String.valueOf(res.getString(7))
                        );
                    }
                }
            }
        }
    }

    /**
      * This prints out the lines for a specific team from each book
      *
      * @param con
      * @param choice
      */
    def searchTeam(con: Connection, choice: Int): Unit = {
        var goodTeam = false
        while(!goodTeam){
            print("Which team would you like to find: ")
            val team: String = StdIn.readLine()
            var sql = ""
            val stmt = con.createStatement();
            val teamTest = stmt.executeQuery("select get_json_object(json, '$.home_team'), " +
                                            "get_json_object(json, '$.away_team') from h2h" +
                        " where get_json_object(json,'$.home_team') = \"" + team + "\" or get_json_object(json,'$.away_team') = \"" + team + "\"")
            if(!teamTest.next()){
                println("Invalid team name, try again")
            }
            else{
                choice match{
                    case 1 => {
                        //team h2h
                        sql += "select get_json_object(json,'$.bookmakers[0].markets.outcomes[0].name')," +
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].price'), " + 
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].name')," +
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].price') from h2h " +
                          "where get_json_object(json,'$.home_team') = \"" + team + "\" or get_json_object(json,'$.away_team') = \"" + team + "\""
                        val res = stmt.executeQuery(sql)
                        printf("%-22s%-22s%-22s%-22s\n", "Home Team", "Line", "Away Team", "Line")
                        while (res.next()) {
                            if(res.getString(1) != null){
                                System.out.printf("%-22s%-22s%-22s%-22s\n",
                                    String.valueOf(res.getString(1)), 
                                    String.valueOf(res.getString(2)),
                                    String.valueOf(res.getString(3)),
                                    String.valueOf(res.getString(4))
                                );
                            }
                        }
                        goodTeam = true

                    }
                    case 2 => {
                        //team spread
                        sql += "select get_json_object(json,'$.bookmakers[0].markets.outcomes[0].name')," +
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].price')," + 
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].point')," +
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].name')," +
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].price')," +
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].point') from spreads " +
                          "where get_json_object(json,'$.home_team') = \"" + team + "\" or get_json_object(json,'$.away_team') = \"" + team + "\""

                        val res = stmt.executeQuery(sql);
                        printf("%-22s%-22s%-13s%-22s%-22s%-13s\n", "Home Team", "Line", "Margin", "Away Team", "Line", "Margin")
                        while (res.next()) {
                            if(res.getString(1) != null){
                                System.out.printf("%-22s%-22s%-13s%-22s%-22s%-13s\n",
                                    String.valueOf(res.getString(1)),
                                    String.valueOf(res.getString(2)),
                                    String.valueOf(res.getString(3)),
                                    String.valueOf(res.getString(4)),
                                    String.valueOf(res.getString(5)),
                                    String.valueOf(res.getString(6))
                                );
                            }
                        }
                        goodTeam = true
                    }
                    case 3 => {
                        //team over
                        sql += "select get_json_object(json,'$.home_team')," +
                          "get_json_object(json,'$.away_team'), " + 
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].name')," +
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].price')," +
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].name'), " +
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].price'), " +
                          "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].point') from totals " +
                          "where get_json_object(json,'$.home_team') = \"" + team + "\" or get_json_object(json,'$.away_team') = \"" + team + "\""

                        val res = stmt.executeQuery(sql);
                        printf("%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n", "Home Team", "Away Team", "Over/Under", "Line", "Over/Under", "Line", "Total Score")
                        while (res.next()) {
                            if(res.getString(3) != null){
                                System.out.printf("%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n",
                                    String.valueOf(res.getString(1)), 
                                    String.valueOf(res.getString(2)),
                                    String.valueOf(res.getString(3)),
                                    String.valueOf(res.getString(4)),
                                    String.valueOf(res.getString(5)),
                                    String.valueOf(res.getString(6)),
                                    String.valueOf(res.getString(7))
                                );
                            }
                        }
                        goodTeam = true
                    }
                }
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
        var sql = ""
        val stmt = con.createStatement();
        choice match {
            case 1 => {
                var num: Int = 0
                if(findFav){
                    //h2h biggest home favorite
                    sql += "select get_json_object(json,'$.bookmakers[0].markets.outcomes[0].name')," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].price') line, " + 
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].name')," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].price') from h2h " +
                      "order by line asc"
                    val res = stmt.executeQuery(sql)
                    printf("%-22s%-22s%-22s%-22s\n", "Home Team", "Line", "Away Team", "Line")
                    var lowHome = ""
                    var lowAway = ""
                    var lowHomeLine = 0
                    var lowAwayLine = 0
                    while (res.next()) {
                        if(res.getString(1) != null){
                            if(res.getString(2).toInt < lowHomeLine){
                                    lowHome = String.valueOf(res.getString(1))
                                    lowAway = String.valueOf(res.getString(3))
                                    lowHomeLine = res.getString(2).toInt
                                    lowAwayLine = res.getString(4).toInt
                            }
                        }

                    }
                    System.out.printf("%-22s%-22s%-22s%-22s\n",
                        lowHome, 
                        lowHomeLine.toString(),
                        lowAway,
                        lowAwayLine.toString()
                    );

                }
                else{
                    //h2h biggest home underdog
                    sql += "select get_json_object(json,'$.bookmakers[0].markets.outcomes[0].name')," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].price') line, " + 
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].name')," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].price') from h2h " +
                      "order by line desc"
                    val res = stmt.executeQuery(sql)
                    printf("%-22s%-22s%-22s%-22s\n", "Home Team", "Line", "Away Team", "Line")
                    var lowHome = ""
                    var lowAway = ""
                    var lowHomeLine = 0
                    var lowAwayLine = 0
                    while (res.next()) {
                        if(res.getString(1) != null){
                            if(res.getString(2).toInt > lowHomeLine){
                                lowHome = String.valueOf(res.getString(1))
                                lowAway = String.valueOf(res.getString(3))
                                lowHomeLine = res.getString(2).toInt
                                lowAwayLine = res.getString(4).toInt
                            }
                        }

                    }
                    System.out.printf("%-22s%-22s%-22s%-22s\n",
                        lowHome, 
                        lowHomeLine.toString(),
                        lowAway,
                        lowAwayLine.toString()
                    );
                }
            }
            case 2 => {
                if(findFav){
                    //spread home biggest favorite
                    sql += "select get_json_object(json,'$.bookmakers[0].markets.outcomes[0].name')," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].price') line," + 
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].point') margin," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].name')," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].price')," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].point') from spreads " +
                      "order by margin desc, line asc"
                    val res = stmt.executeQuery(sql)
                    printf("%-22s%-22s%-13s%-22s%-22s%-13s\n", "Home Team", "Line", "Margin", "Away Team", "Line", "Margin")
                    var lowHome = ""
                    var lowAway = ""
                    var homeMargin = 0.0
                    var awayMargin = 0.0
                    var lowHomeLine = 0
                    var lowAwayLine = 0
                    while (res.next()) {
                        if(res.getString(1) != null){
                            if(res.getString(2).toInt < lowHomeLine){
                                lowHome = String.valueOf(res.getString(1))
                                lowAway = String.valueOf(res.getString(4))
                                homeMargin = res.getString(3).toDouble
                                lowHomeLine = res.getString(2).toInt
                                lowAwayLine = res.getString(5).toInt
                                awayMargin = res.getString(6).toDouble
                            }
                        }

                    }
                    System.out.printf("%-22s%-22s%-13s%-22s%-22s%-13s\n",
                        lowHome, 
                        lowHomeLine.toString(),
                        homeMargin.toString(),
                        lowAway,
                        lowAwayLine.toString(),
                        awayMargin.toString()
                    );

                }
                else{
                    //spread home biggest underdog
                    sql += "select get_json_object(json,'$.bookmakers[0].markets.outcomes[0].name')," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].price') line," + 
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].point') margin," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].name')," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].price')," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].point') from spreads " +
                      "order by margin desc, line desc"
                    val res = stmt.executeQuery(sql)
                    printf("%-22s%-22s%-13s%-22s%-22s%-13s\n", "Home Team", "Line", "Margin", "Away Team", "Line", "Margin")
                    var lowHome = ""
                    var lowAway = ""
                    var homeMargin = 0.0
                    var awayMargin = 0.0
                    var lowHomeLine = 0
                    var lowAwayLine = 0
                    while (res.next()) {
                        if(res.getString(1) != null){
                            if(res.getString(2).toInt > lowHomeLine){
                                lowHome = String.valueOf(res.getString(1))
                                lowAway = String.valueOf(res.getString(4))
                                homeMargin = res.getString(3).toDouble
                                lowHomeLine = res.getString(2).toInt
                                lowAwayLine = res.getString(5).toInt
                                awayMargin = res.getString(6).toDouble
                            }
                        }

                    }
                    System.out.printf("%-22s%-22s%-13s%-22s%-22s%-13s\n",
                        lowHome, 
                        lowHomeLine.toString(),
                        homeMargin.toString(),
                        lowAway,
                        lowAwayLine.toString(),
                        awayMargin.toString()
                    );
                }
            }
            case 3 => {
                if(findFav){
                    //biggest over
                    sql += "select get_json_object(json,'$.home_team')," +
                      "get_json_object(json,'$.away_team'), " + 
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].name')," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].price') line," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].name'), " +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].price'), " +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].point') point from totals " +
                      "order by point desc"
                    val res = stmt.executeQuery(sql);
                    printf("%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n", "Home Team", "Away Team", "Over/Under", "Line", "Over/Under", "Line", "Total Score")
                    while (res.next()) {
                        if(res.getString(3) != null){
                            System.out.printf("%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n",
                                String.valueOf(res.getString(1)), 
                                String.valueOf(res.getString(2)),
                                String.valueOf(res.getString(3)),
                                String.valueOf(res.getString(4)),
                                String.valueOf(res.getString(5)),
                                String.valueOf(res.getString(6)),
                                String.valueOf(res.getString(7))
                            );
                        }
                    }
                }
                else{
                    //smallest over
                    sql += "select get_json_object(json,'$.home_team')," +
                      "get_json_object(json,'$.away_team'), " + 
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].name')," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[0].price') line," +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].name'), " +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].price'), " +
                      "get_json_object(json,'$.bookmakers[0].markets.outcomes[1].point') point from totals " +
                      "order by point asc"
                    val res = stmt.executeQuery(sql);
                    printf("%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n", "Home Team", "Away Team", "Over/Under", "Line", "Over/Under", "Line", "Total Score")
                    while (res.next()) {
                        if(res.getString(3) != null){
                            System.out.printf("%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n",
                                String.valueOf(res.getString(1)), 
                                String.valueOf(res.getString(2)),
                                String.valueOf(res.getString(3)),
                                String.valueOf(res.getString(4)),
                                String.valueOf(res.getString(5)),
                                String.valueOf(res.getString(6)),
                                String.valueOf(res.getString(7))
                            );
                        }
                    }
                }
            }
        }
    }
}