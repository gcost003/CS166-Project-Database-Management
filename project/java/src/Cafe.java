/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Cafe {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Cafe
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Cafe(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Cafe

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Cafe.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Cafe esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Cafe object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Cafe (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Goto Menu");
                System.out.println("2. Update Profile");
                System.out.println("3. Place a Order");
                System.out.println("4. Update a Order");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: Menu(esql, authorisedUser); break;
                   case 2: UpdateProfile(esql, authorisedUser); break;
                   case 3: PlaceOrder(esql, authorisedUser); break;
                   case 4: UpdateOrder(esql, authorisedUser); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    **/
   public static void CreateUser(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();
         
	    String type="Customer";
	    String favItems="";

				 String query = String.format("INSERT INTO USERS (phoneNum, login, password, favItems, type) VALUES ('%s','%s','%s','%s','%s')", phone, login, password, favItems, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

 // Rest of the functions definition go in here

 /*I am not to sure is I need to put "sting authorisedUser" in the void function too  */
  public static void Menu(Cafe esql, String authorisedUser){
      try{
         /*Trying to display all item names and prices from menu */
         // we can ask the user if he wants to see the menu?
         System.out.println("===========================================================");
         boolean makeSure = true;
         while(makeSure == true){
            System.out.println("Do you want to see our Cafe Menu? Yes or No");
            String seeMenu = in.readLine();
            if("Yes".equalsIgnoreCase(seeMenu)){
               String query = String.format("SELECT * FROM MENU"); 
               esql.executeQueryAndPrintResult(query);
            }
            else if("No".equalsIgnoreCase(seeMenu)){
               makeSure = false;
            }
            else{
               System.out.println("Sorry Invalid input. Try again");
            }
         }
         System.out.println("====================================");
         System.out.println("1. Search For Item");
         System.out.println("2. Edit Menu");
         System.out.println("====================================");
         System.out.println("9. Go Back");
         boolean menuYes = true;
         while(menuYes){
            switch(readChoice()){
               case 1: SearchItem(esql);break;  
               case 2: UpdateMenu(esql, authorisedUser);break;
               case 9: menuYes = false; break; 
               default : System.out.println("Unrecognized Choice!");break;
            }
         }
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }     
   }
      
  /*Made a list of commands for the user to update his/her profile */    
  public static void UpdateProfile(Cafe esql, String authorisedUser){
      try{
         boolean profile = true;
         while(profile){
            System.out.println("---Updating Profile---");
            System.out.println("====================================");
            System.out.println("1. Change Password");
            System.out.println("2. Change Phone Number");
            System.out.println("3. Change Favorite Item");
            System.out.println("4. Change User Type Authority");
            System.out.println("====================================");
            System.out.println("9. Go Back");
            switch (readChoice()){
               case 1: ChangePassword(esql, authorisedUser);break;
               case 2: ChangePhoneNumber(esql, authorisedUser);break;
               case 3: ChangeFavItem(esql, authorisedUser);break;
               case 4: ChangeType(esql, authorisedUser);break;
               case 9: profile = false; break;
               default : System.out.println("Unrecognized Choice!"); break;
            }
         }
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }   
  }
 /* first show the previous orders(5 most recent for customers) or orders from the past 24 hours for the managers and employees), then 
    place new order */
  public static void PlaceOrder(Cafe esql, String authorisedUser){  //this is basically adding your order
      try{
         String check = String.format("SELECT * FROM Users WHERE login ='%s' AND type = 'Customer'", authorisedUser);
         if(esql.executeQueryAndReturnResult(check).isEmpty()){
            String query = String.format("SELECT * FROM Orders WHERE (timeStampRecieved=NOW())< 86400");
            //or you can do this for String query = String.format("SELECT * FROM Orders WHERE timeStampReceived > (NOW() - INTERVAL 24 HOUR)");
            esql.executeQueryAndPrintResult(query);
         }
         else{
            String check = String.format("Showing the previous orders (5 most recent) for Customers.");
            String query = String.format("SELECT * FROM Orders WHERE orderid>=(orderid-5) AND login = '%s'", authorisedUser);
            //"SELECT * FROM Orders WHERE orderid>=(orderid-5)"
            esql.executeQueryAndPrintResult(query);  
         }

         System.out.println("---Placing an Order---");
         System.out.println("=============================");
         boolean loop = true;
         while(loop==true){
            System.out.println("Do you want to Add a Order? (Yes/No)");
            String edit = in.readLine();
            if("Yes".equalsIgnoreCase(edit)){
               System.out.println("---Adding Order---");
               System.out.println("Enter itemName:");
               String item=in.readLine();
               String cur=String.format("SELECT price FROM Menu WHERE itemName='%s'",item);
               esql.executeQueryAndPrintResult(cur);
               System.out.println("If you are Tipping, then Enter the Price amount with Tip: ");
               System.out.println("If not, Enter the Price of the item: ");
               String tipWithPrice = in.readLine();
               Float total = Float.parseFloat(tipWithPrice);
               Long datetime = System.currentTimeMillis();
               Timestamp curtime = new Timestamp(datetime); 
               boolean isPaid= false;
               String NewOrder = String.format("INSERT INTO Orders (orderid,login,paid,timeStampRecieved,total) VALUES('%s','%s','%s','%f')",orderid,authorisedUser,isPaid,curtime,total);  //we might not need orderid sense it generates automaticlly 
               esql.executeUpdate(NewOrder); 
               String NewOrderID = String.format("SELECT * FROM Orders WHERE login='%s'",authorisedUser);
               esql.executeQueryAndPrintResult(NewOrderID);
               System.out.println("Successfully placed order!");
            }
            else if("No".equalsIgnoreCase(edit)){
               loop=false;
            }
            else{
               System.out.println("Invalid Input, try again: ");
            }
         }
         // if("Yes".equalsIgnoreCase(edit)){
         //    System.out.println("---Adding Order---");
         //    System.out.println("How many items you want to order?");
         //    String input = in.readLine(); //convert string into integer
         //    int orderNumbers = Integer.parseInt(input);
         //    float total=0;
         //    float newcur;
         //    for(int i=0;i<orderNumbers;i++){
         //       System.out.println("Enter itemName:");
         //       String item=in.readLine();
         //       String cur=String.format("SELECT price FROM Menu WHERE itemName='%s'",item);
         //       newcur=Float.intBitsToFloat(esql.executeQueryAndPrintResult(cur));
         //       total=total+(newcur*1.421428571*Math.pow(10,-45));
         //    }
         //     Long datetime = System.currentTimeMillis();
         //     Timestamp curtime = new Timestamp(datetime); 
         //     boolean isPaid= false;
         //    String NewOrder = String.format("INSERT INTO Orders (orderid,login,paid,timeStampRecieved,total) VALUES('%s','%s','%s','%f')",orderid,authorisedUser,isPaid,curtime,total);  //we might not need orderid sense it generates automaticlly 
         //    esql.executeUpdate(NewOrder); 
         //    String NewOrderID = String.format("SELECT * FROM Orders WHERE login='%s'",authorisedUser);
         //    esql.executeQueryAndPrintResult(NewOrderID);

         //    //System.out.println("Your orderid is");
         //    //System.out.println(orderid + NewOrder);
         //    System.out.println("Successfully placed order!");
         // }   
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }



  public static void UpdateOrder(Cafe esql, String authorisedUser){  
     try{
         System.out.println("---Updating Order---");
         System.out.println("=====================================");
         String check=String.format("SELECT type FROM Users WHERE login = '%s' AND type='Customer'",authorisedUser);
         
         if(esql.executeQueryAndReturnResult(check).isEmpty()){
            System.out.println("Enter the Order ID you want to update:");
            String orderid=in.readLine();
            //int serialNum = Serial.write(orderid);
            // System.out.println("Has the order been paid: 'True' or 'False' (type True or False)");
            // String userInput = in.readLine();
            // if('True'.equalsIgnoreCase(userInput)){
               boolean paidstatus=true;
               String query=String.format("UPDATE Orders SET paid='%s' WHERE orderid='%s'",paidstatus,orderid);
               esql.executeUpdate(query);
               System.out.println("Successfully Updated Order!");
            //}
            // else if('False'.equalsIgnoreCase(userInput)){return;}
         }
         else{
            System.out.println("You are a customer, please inform the manager to update your order.");
         }
     }
     catch(Exception e){
      System.err.println (e.getMessage ());
   }
  }

  public static void ChangePassword(Cafe esql, String authorisedUser){
      try{
         System.out.println("---We Are Now Changing Password--- ");
         System.out.println("\tPlease Enter New Password: ");
         String newPass = in.readLine();

         String query = String.format("UPDATE Users SET password = '%s' WHERE login = '%s'", newPass, authorisedUser);
         esql.executeUpdate(query);
         System.out.println("Passord is now Changed!");
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
  }

  public static void ChangePhoneNumber(Cafe esql, String authorisedUser){
      try{
         System.out.println("---We Are Now Changing Phone Number--- ");
         System.out.println("\tPlease Enter New Phone Number: ");
         String newPhoneNum = in.readLine();

         String query = String.format("UPDATE Users SET phoneNum = '%s' WHERE login = '%s'", newPhoneNum, authorisedUser);
         esql.executeUpdate(query);
         System.out.println("Phone Number is now Changed!");
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
  }

  public static void ChangeFavItem(Cafe esql, String authorisedUser){
   try{
      System.out.println("---We Are Now Changing Favorite Item--- ");
      System.out.println("\tPlease Enter New Favorite Item: ");
      String newFavItem = in.readLine();

      String query = String.format("UPDATE Users SET favItems = '%s' WHERE login = '%s'", newFavItem, authorisedUser);
      esql.executeUpdate(query);
      System.out.println("Your Favorite Item is now Changed!");
   }
   catch(Exception e){
      System.err.println (e.getMessage ());
   }

  }

  /* Made sure that the authorisedUser is a manager then asked customer login in order to be able 
     to change the customer to employe or manager */
  public static void ChangeType(Cafe esql, String authorisedUser){
   try{  
      System.out.println("---Checking if you are a manager. Please Wait-- ");
      String query = String.format("SELECT type FROM Users WHERE login ='%s' AND type = 'Manager '", authorisedUser);
       //might not need this if we figure out the conversion="M" thing
      if(esql.executeQueryAndReturnResult(query).isEmpty()){
         System.out.println("You are not a manager So you are not able to change the types of autorization.");
      }
      else{
            System.out.println("---Your Are a Manager! You're able to set other User from Customer to Manger or to Employee. --- ");
            System.out.println("====================================");
            System.out.println("1. Change Customer to Employee");
            System.out.println("2. Change Customer to Manager");
            System.out.println("====================================");
            System.out.println("\tEither enter \"1\" or \"2\"");
            String choice = in.readLine();
            //String option1 = "1";  we can use option1 for the if statement like--> if(option1.equals(choice)){} just in case the other dont works
            if("1".equals(choice)){
               boolean checkLogin2 = true;
               while(checkLogin2){
                  System.out.println("Enter the User login of the Customer that you want to update there type:");
                  String login = in.readLine();
                  String checkID=String.format("SELECT * FROM Users WHERE login='%s' AND type='Customer'",login);
                  if(esql.executeQueryAndReturnResult(checkID).isEmpty()){
                     System.out.println("Invalid Login! Try again");
                  }
                  else{
                     String query2 = String.format("UPDATE Users SET type = 'Employee' WHERE login = '%s'", login);
                     esql.executeUpdate(query2);
                     System.out.println("Your Authority Type is now Changed!");
                     checkLogin2 = false;
                  }
               }
            }
            //String option2 = "2";  we can use option2 for the if statement like--> if(option2.equals(choice)){} just in case 
            else if("2".equals(choice)){
               boolean checkLogin = true;
               while(checkLogin == true){ //do if statements instead
                  System.out.println("Enter the User login of the Customer that you want to update there type:");
                  String login = in.readLine();
                  String checkID=String.format("SELECT * FROM Users WHERE login='%s' AND type='Customer'",login);
                  if(esql.executeQueryAndReturnResult(checkID).isEmpty()){
                     System.out.println("Invalid Login! Try again");
                  }
                  else{
                     String query3 = String.format("UPDATE Users SET type = 'Manager' WHERE login = '%s'", login);
                     esql.executeUpdate(query3);
                     System.out.println("Your Authority Type is now Changed!");
                     checkLogin = false;
                  }
               }
               //maybe ask if you want to see results
            }      
         }
      }
   catch(Exception e){
         System.err.println (e.getMessage ());
      } 
  }

  public static void SearchItem(Cafe esql){
   try{
      System.out.print("\t Please select the chiose of search for items by \"Name\" or \"Type\": ");
      String check = in.readLine();
      if("Name".equalsIgnoreCase(check)) {
        System.out.println("\tEnter The Item name you are searching: ");
        String itemName = in.readLine();
        String query = String.format("SELECT M.itemName, M.price FROM MENU M WHERE M.itemName = '%s'", itemName);
      esql.executeQueryAndPrintResult(query);
      }
      else{
        System.out.println("\tEnter The Item Type of what you are searching: ");
        String itemType = in.readLine();
        String query = String.format("SELECT M.itemName, M.price FROM MENU M WHERE M.type = '%s'", itemType);
      esql.executeQueryAndPrintResult(query);
      }
     }catch(Exception e){
     System.err.println (e.getMessage ());
   }
  }

  public static void UpdateMenu(Cafe esql, String authorisedUser){
      try{
         //I think our problem is our <SELECT type> 
         String query1 = String.format("SELECT type FROM Users WHERE login ='%s' AND type='Manager'", authorisedUser);
         
         if(esql.executeQueryAndReturnResult(query1).isEmpty()){
            System.out.println("You are not a manager.");
         }
         else{
            System.out.println("Do you want to \"Add\", \"Delete\" or \"Update\" an item?");
            String edit = in.readLine();
            if("Add".equalsIgnoreCase(edit)){
               System.out.println("---Adding item---");
               System.out.println("Enter ItemName:");
               String ItemName = in.readLine();
               System.out.println("Enter Type:");
               String Type = in.readLine();
               System.out.println("Enter Price:");
               String curPrice = in.readLine();
               float Price=Float.parseFloat(curPrice);
               System.out.println("Enter Description:");
               String Description = in.readLine();
               System.out.println("Enter Image URL:");
               String URL = in.readLine();

               String query = String.format("INSERT INTO MENU (itemName,type,price,description,imageURL) VALUES('%s','%s','%f','%s','%s')",ItemName,Type,Price,Description,URL);
               esql.executeUpdate(query);
               System.out.println("Successfully added the item!");
               
            }
            else if("Delete".equalsIgnoreCase(edit)){
               System.out.println("-------------------------------------Deleting item------------------------------------");
               System.out.println("Enter ItemName that you want to delete:");
               String ItemName=in.readLine();
               // String delForeignKey = String.format("DELETE FROM ItemStatus WHERE itemName='%s'",ItemName);
               // esql.executeQuery(delForeignKey);
               String query = String.format("DELETE FROM MENU WHERE itemName='%s'",ItemName);
               esql.executeUpdate(query);
               System.out.print("Successfully deleted the item!");
            }
            else if("Update".equalsIgnoreCase(edit)){
               System.out.println("------------------------------Updating item----------------------------------");
             
                  System.out.println("Enter ItemName that you want to update:");
                  String ItemName=in.readLine();
                  boolean looping = true;
                  while(looping == true){
                  String checkitemname=String.format("SELECT * FROM Menu WHERE itemName='%s'",ItemName);
                  if(esql.executeQueryAndReturnResult(checkitemname).isEmpty()){
                     System.out.println("Invalid Itemname, Try again");
                  }
                  else{
                     looping = false;
                  }
               }
               System.out.println("What do you want to update, ItemName, Type, Price, Description or URL?");
               String input=in.readLine();
               if("ItemName".equalsIgnoreCase(input)){
                  System.out.println("Enter New ItemName:");
                  String newname=in.readLine();
                  String query=String.format("UPDATE MENU SET itemName='%s' WHERE itemName='%s'",newname,ItemName);
                  esql.executeUpdate(query);
               }
               else if("Type".equalsIgnoreCase(input)){
                  System.out.println("Enter New Type:");
                  String newtype=in.readLine();
                  String query=String.format("UPDATE MENU SET type='%s' WHERE itemName='%s'",newtype,ItemName);
                  esql.executeUpdate(query);
               }
               else if("Price".equalsIgnoreCase(input)){
                  System.out.println("Enter New Price:");
                  String curnewprice=in.readLine();
                  Float newprice=Float.parseFloat(curnewprice);
                  String query=String.format("UPDATE MENU SET price='%f' WHERE itemName='%s'",newprice,ItemName);
                  esql.executeUpdate(query);
               }
               else if("Description".equalsIgnoreCase(input)){
                  System.out.println("Enter New Description:");
                  String newdescription=in.readLine();
                  String query=String.format("UPDATE MENU SET description='%s' WHERE itemName='%s'",newdescription,ItemName);
                  esql.executeUpdate(query);
               }
               else if("URL".equalsIgnoreCase(input)){
                  System.out.println("Enter New URL:");
                  String newURL=in.readLine();
                  String query=String.format("UPDATE MENU SET imageURL='%s' WHERE itemName='%s'",newURL,ItemName);
                  esql.executeUpdate(query);
               }
            }
         }
     }
     catch(Exception e){
      System.err.println (e.getMessage ());
    }
  }

}//end Cafe
