/*============================================================================
YrbAppsub: A JDBC APP to list total sales for a customer from the YRB DB.
============================================================================*/

import java.util.*;
import java.net.*;
import java.text.*;
import java.lang.*;
import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/*============================================================================
CLASS CustTotal
============================================================================*/

public class Yrbapp {
    private Connection conDB;   // Connection to the database system.
    private String url;         // URL: Which database?
    Scanner userInput = new Scanner(System.in);
    Scanner answerInput = new Scanner(System.in);
    private Integer custID;     // Who are we tallying?
    private String  custName;   // Name of that customer.
    private String  custCity;   // City of the customer.
    private char answer;
    private String nameAnswer;
    private String cityAnswer;
    private String category;
    Scanner catAnswer = new Scanner(System.in); 
    Scanner bookAnswer = new Scanner(System.in);
    public String catAnswers;
    private int bookAnswers;
    private int quantity;
    private String title;
    private String titles;
    private String language;
    String[] stringValues = new String[100];
    List<String> list = Arrays.asList(stringValues);
    String[] stringTitles = new String[100];
    String[] stringLangs = new String[100];
    int[] years = new int[100];
    int[] weights = new int[100];
    int count = 0;
    double minimum = 0;
    private String club;
    
    Calendar calendar = Calendar.getInstance();
    java.sql.Timestamp ourJavaTimestampObject = new java.sql.Timestamp(calendar.getTime().getTime());

    
    // Constructor
    public Yrbapp (String[] args) {
        // Set up the DB connection.
        try {
            // Register the driver with DriverManager.
            Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // URL: Which database?
        url = "jdbc:db2:c3421a";

        // Initialize the connection.
        try {
            // Connect with a fall-thru id & password
            conDB = DriverManager.getConnection(url);
        } catch(SQLException e) {
            System.out.print("\nSQL: database connection error.\n");
            System.out.println(e.toString());
            System.exit(0);
        }    

        // Let's have autocommit turned off.  No particular reason here.
        try {
            conDB.setAutoCommit(false);
        } catch(SQLException e) {
            System.out.print("\nFailed trying to turn autocommit off.\n");
            e.printStackTrace();
            System.exit(0);
        }    

        // Who are we tallying?
        if (args.length != 1) {
            // Don't know what's wanted.  Bail.
            System.out.println("\nUsage: java CustTotal cust#");
            System.exit(0);
        } else {
            try {
                custID = new Integer(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("\nUsage: java CustTotal cust#");
                System.out.println("Provide an INT for the cust#.");
                System.exit(0);
            }
        }

        // Is this custID for real?
        while(!customerCheckExists()) {
            System.out.print("There is no customer #");
            System.out.print(custID);
            System.out.println(" in the database.");
            System.out.println("Please enter a customer id from 1-45");
	    custID = userInput.nextInt();
        }

        reportCustomerInformation();

        // Commit.  Okay, here nothing to commit really, but why not...
        try {
            conDB.commit();
        } catch(SQLException e) {
            System.out.print("\nFailed trying to commit.\n");
            e.printStackTrace();
            System.exit(0);
        }    
        // Close the connection.
        try {
            conDB.close();
        } catch(SQLException e) {
            System.out.print("\nFailed trying to close the connection.\n");
            e.printStackTrace();
            System.exit(0);
        }    

    }

    public boolean customerCheckExists() {
        String            customerExist = "";     // The SQL text.
        PreparedStatement querySt   = null;   // The query handle.
        ResultSet         answers   = null;   // A cursor.

        boolean           inDB      = false;  // Return.

        customerExist =
            "SELECT name       "
          + "FROM yrb_customer "
          + "WHERE cid = ?     ";

        // Prepare the query.
        try {
            querySt = conDB.prepareStatement(customerExist);
        } catch(SQLException e) {
            System.out.println("SQL#1 failed in prepare");
            System.out.println(e.toString());
            System.exit(0);
        }

        // Execute the query.
        try {
            querySt.setInt(1, custID.intValue());
            answers = querySt.executeQuery();
        } catch(SQLException e) {
            System.out.println("SQL#1 failed in execute");
            System.out.println(e.toString());
            System.exit(0);
        }

        // Any answer?
        try {
            if (answers.next()) {
                inDB = true;
                custName = answers.getString("name");
            } else {
                inDB = false;
                custName = null;
            }
        } catch(SQLException e) {
            System.out.println("SQL#1 failed in cursor.");
            System.out.println(e.toString());
            System.exit(0);
        }

        // Close the cursor.
        try {
            answers.close();
        } catch(SQLException e) {
            System.out.print("SQL#1 failed closing cursor.\n");
            System.out.println(e.toString());
            System.exit(0);
        }

        // We're done with the handle.
        try {
            querySt.close();
        } catch(SQLException e) {
            System.out.print("SQL#1 failed closing the handle.\n");
            System.out.println(e.toString());
            System.exit(0);
        }

        return inDB;
    }

    public void reportCustomerInformation() {
        String            Find_Customer = "";     // The SQL text.
        PreparedStatement querySt   = null;   // The query handle.
        ResultSet         answers   = null;   // A cursor.

        Find_Customer =
            "SELECT P.city as custCity                      "
          + "    FROM yrb_customer P                        "
          + "    WHERE P.cid = ?                            ";
          

        // Prepare the query.
        try {
            querySt = conDB.prepareStatement(Find_Customer);
        } catch(SQLException e) {
            System.out.println("SQL#2 failed in prepare");
            System.out.println(e.toString());
            System.exit(0);
        }

        // Execute the query.
        try {
            querySt.setInt(1, custID.intValue());
            answers = querySt.executeQuery();
        } catch(SQLException e) {
            System.out.println("SQL#2 failed in execute");
            System.out.println(e.toString());
            System.exit(0);
        }

    
        // Walk through the results and present them.
     	try {
            System.out.print(" CID: #" + custID + " ");
            System.out.print(" is customer: " + custName + " and lives in: ");
	    if (answers.next()) {
		  custCity = answers.getString("custCity");
		  System.out.print(custCity);

		}
            System.out.println(".");
	    } catch(SQLException e) {
             	System.out.println("SQL#2 failed in cursor.");
             	System.out.println(e.toString());
             	System.exit(0);
        }
        

	
	System.out.print("Would you like to update the customer information? [y/n]");
    answer = answerInput.next().charAt(0);
  while(!(answer == 'y' || answer == 'n')){
      System.out.println("please enter y or n");
      answer = answerInput.next().charAt(0);
  }
	if(answer == 'y'){
      System.out.println("Update customer name?[y/n] ");
      answer = answerInput.next().charAt(0);
    while(!(answer == 'y' || answer == 'n')){
        System.out.println("please enter y or n");
        answer = answerInput.next().charAt(0);
    }
      if(answer == 'y'){
        System.out.print("Enter customer name:");
        answerInput.nextLine();
        nameAnswer = answerInput.nextLine();
        if(nameAnswer instanceof String){
            System.out.println("Customer name updated");
            update_customer();
        }
    }
    
        System.out.println("Update city name?[y/n]   ");
       answer = answerInput.next().charAt(0);
       while(!(answer == 'y' || answer == 'n')){
        System.out.println("please enter y or n");
        answer = answerInput.next().charAt(0);
    }
        if(answer == 'y'){
            answerInput.nextLine();
            System.out.print("Enter city name:");
            cityAnswer = answerInput.nextLine();
            if(cityAnswer instanceof String){
                System.out.println("City name updated");
                update_customer();

                 
                try {
                conDB.commit();
            } catch(SQLException e) {
                System.out.print("\nFailed trying to commit.\n");
                e.printStackTrace();
                System.exit(0);
              }  
            }  
          }
     }


      // continue the program content
	  System.out.println("Choose a category from the list");
	  reportCategoryInformation();
	  System.out.println("Choose a category from the list");
      catAnswers = catAnswer.nextLine();
      while(!list.contains(catAnswers)){
        System.out.println("Category not found,please enter a category from the given list");
        catAnswers = catAnswer.nextLine();
      }
	  if(list.contains(catAnswers)){
	  System.out.println("You have chosen " + catAnswers + " ");
	  reportBookInformation();

      System.out.println("Now enter the number of a book");
      do{
        try {
            bookAnswers = bookAnswer.nextInt();
            while(bookAnswers == 0){
                System.out.println("please enter a number thats not 0");
                bookAnswers = bookAnswer.nextInt();
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid number");
            bookAnswer.next();
        }
    } while(bookAnswers<1);
        
          System.out.println("You have chosen " + bookAnswers + " ");
			if(bookAnswers <= count || bookAnswers > 0){
                System.out.println(stringTitles[bookAnswers] + "   language: "  +  stringLangs[bookAnswers] + "  year: " + years[bookAnswers]);
                System.out.println("Here is the price");
                min_price();	
                System.out.println("Enter a desired quantity:");
                do{
                    try {
                        quantity = bookAnswer.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid number");
                        bookAnswer.next();
                    }
                } while(quantity<0);
                double total = quantity * minimum;
                String formattedDouble = String.format("%.2f",total);
                System.out.println("Total is:"+ formattedDouble);
                if(total == 0.00){
                    System.out.println("Have a good day");
                    System.exit(0);
                }
                System.out.println("Would you like to make a purchase?[y/n]");
                answer = answerInput.next().charAt(0);
                while(!(answer == 'y' || answer == 'n')){
                    System.out.println("please enter y or n");
                    answer = answerInput.next().charAt(0);
                }
                if(answer == 'y'){
                    System.out.println("Thank you for shopping, have a nice day!");
                    insert_purchase();
                }
                if(answer == 'n'){
                    System.out.println("Have a good day");
                }               
		 }else{
          System.exit(0);
	    }
    }   

        // Close the cursor.
        try {
            answers.close();
        } catch(SQLException e) {
            System.out.print("SQL#2 failed closing cursor.\n");
            System.out.println(e.toString());
            System.exit(0);
        }

        // We're done with the handle.
        try {
            querySt.close();
        } catch(SQLException e) {
            System.out.print("SQL#2 failed closing the handle.\n");
            System.out.println(e.toString());
            System.exit(0);
        }

    }
    public void update_customer(){
        String    update_customer =   "";
        PreparedStatement querySt = null;
        int         answers = 0;

        update_customer = 
        "UPDATE yrb_customer                                ";

        if(cityAnswer instanceof String && nameAnswer instanceof String){
            update_customer +=  "SET name ='"+nameAnswer+"', city = '"+cityAnswer+"' WHERE yrb_customer.cid=?";            
                       
         } else if (nameAnswer instanceof String){
            update_customer +=  "SET name ='"+nameAnswer+"'  WHERE yrb_customer.cid = ?";
        
         } else if (cityAnswer instanceof String){
                update_customer +=     "SET city ='"+cityAnswer+"'  WHERE yrb_customer.cid = ? ";
                
        }
    
     try{
         querySt = conDB.prepareStatement(update_customer);
     }
     catch(SQLException e) {
        System.out.println("SQL#3 failed in prepare");
        System.out.println(e.toString());
        System.exit(0);
    }
  
    try {
        querySt.setInt(1, custID.intValue());
        answers = querySt.executeUpdate();
    } catch(SQLException e) {
        System.out.println("SQL#3 failed in update");
        System.out.println(e.toString());
        System.exit(0);
    }
   

    // We're done with the handle.
    try {
        querySt.close();
    } catch(SQLException e) {
        System.out.print("SQL#3 failed closing the handle.\n");
        System.out.println(e.toString());
        System.exit(0);
    }



    }
	

  	public void reportCategoryInformation() {
       	 	String        Fetch_Category =   "";   // The SQL text.
       		 PreparedStatement querySt   = null;   // The query handle.
       		 ResultSet         answers   = null;   // A cursor.

      	         Fetch_Category =
        	    "SELECT D.cat as category                       "
         	  + "    FROM yrb_category D                        ";
	
	int i=0;
	
        // Prepare the query.
        try {
            querySt = conDB.prepareStatement(Fetch_Category);
        } catch(SQLException e) {
            System.out.println("SQL#4 failed in prepare");
            System.out.println(e.toString());
            System.exit(0);
        }

        // Execute the query.
        try {
           
            answers = querySt.executeQuery();
			
        } catch(SQLException e) {
            System.out.println("SQL#4 failed in execute");
            System.out.println(e.toString());
            System.exit(0);
        }

        // Walk through the results and present them.
        try {
            while (answers.next()) {
		category = answers.getString("category");
		stringValues[i] = answers.getString("category");
		i++;
	     }
		
	     for(i=0; i < 12; i++){
		System.out.println(stringValues[i]);
            } 
        } catch(SQLException e) {
            System.out.println("SQL#4 failed in cursor.");
            System.out.println(e.toString());
            System.exit(0);
        }

        // Close the cursor.
        try {
            answers.close();
        } catch(SQLException e) {
            System.out.print("SQL#4 failed closing cursor.\n");
            System.out.println(e.toString());
            System.exit(0);
        }

        // We're done with the handle.
        try {
            querySt.close();
        } catch(SQLException e) {
            System.out.print("SQL#4 failed closing the handle.\n");
            System.out.println(e.toString());
            System.exit(0);
        }

    }  


	public void reportBookInformation() {
       	 	String            Find_book = "";     // The SQL text.
       		 PreparedStatement querySt   = null;   // The query handle.
       		 ResultSet         answers   = null;   // A cursor.

      	         Find_book =
        	    "SELECT C.cat as category, B.title as title, B.year as year,B.language as language, B.weight as weight, count('catAnswer') as count   "
         	  + "    FROM yrb_book B, yrb_category C                                                                     "
		  + "	 WHERE B.cat = C.cat AND B.cat ='"+catAnswers+"'"
		  + " GROUP BY C.cat,B.title,B.year,B.language,B.weight ";                                                                                        


        // Prepare the query.
        try {
            querySt = conDB.prepareStatement(Find_book);
        } catch(SQLException e) {
            System.out.println("SQL#5 failed in prepare");
            System.out.println(e.toString());
            System.exit(0);
        }

        // Execute the query.
        try {
           
            answers = querySt.executeQuery();
			
        } catch(SQLException e) {
            System.out.println("SQL#5 failed in execute");
            System.out.println(e.toString());
            System.exit(0);
        }
	int i = 0;
	int year;
	int weight;
        // Walk through the results and present them.
        try {
            while (answers.next()) {
               title = answers.getString("title");
		year = answers.getInt("year");
		weight = answers.getInt("weight");
		language = answers.getString("language");
		stringTitles[i+1] = answers.getString("title");
		stringLangs[i+1] = answers.getString("language");
		years[i+1] = answers.getInt("year");
		weights[i+1] = answers.getInt("weight");
		i++;
		count++;
            } 
		
			for(i=1; i <= count; i++){
                
		System.out.println(i + "  title: " +  stringTitles[i] + "   language: "  +  stringLangs[i] + "  year: " + years[i] + "   weight: " + weights[i]);
             
		
            }

        } catch(SQLException e) {
            System.out.println("SQL#5 failed in cursor.");
            System.out.println(e.toString());
            System.exit(0);
        }

        // Close the cursor.
        try {
            answers.close();
        } catch(SQLException e) {
            System.out.print("SQL#5 failed closing cursor.\n");
            System.out.println(e.toString());
            System.exit(0);
        }

        // We're done with the handle.
        try {
            querySt.close();
        } catch(SQLException e) {
            System.out.print("SQL#5 failed closing the handle.\n");
            System.out.println(e.toString());
            System.exit(0);
        }

    }
    public void min_price() {
        String            min_price = "";     // The SQL text.
        PreparedStatement querySt   = null;   // The query handle.
        ResultSet         answers   = null;   // A cursor.

           min_price =
           "select m.club as clubs, minimum, titles from(SELECT MIN(prices) as minimum, titles FROM (SELECT o.title as titles, o.price as prices FROM yrb_offer as o, yrb_member as m WHERE m.club = o.club AND o.title= ? AND m.cid = ?) group by titles), yrb_offer as o, yrb_member as m where o.price = minimum and o.title = titles and o.club = m.club and m.cid = ?";                                                                                     


// Prepare the query.
try {
    querySt = conDB.prepareStatement(min_price);
} catch(SQLException e) {
    System.out.println("SQL#6 failed in prepare");
    System.out.println(e.toString());
    System.exit(0);
}

// Execute the query.
try {
    querySt.setString(1,stringTitles[bookAnswers]);
    querySt.setInt(2, custID.intValue());
    querySt.setInt(3, custID.intValue());
    answers = querySt.executeQuery();
} catch(SQLException e) {
    System.out.println("SQL#6 failed in execute");
    System.out.println(e.toString());
    System.exit(0);
}
int i = 0;
int cx = 0;
// Walk through the results and present them.
try {
    while (answers.next()) {
       title = answers.getString("titles");
       minimum = answers.getDouble("minimum");
       club = answers.getString("clubs");
       stringTitles[i] = answers.getString("titles");
    cx++;
    } 

  
      
System.out.println("title: " +  stringTitles[bookAnswers] + " price: " + minimum);
       

    

} catch(SQLException e) {
    System.out.println("SQL#6 failed in cursor.");
    System.out.println(e.toString());
    System.exit(0);
}

// Close the cursor.
try {
    answers.close();
} catch(SQLException e) {
    System.out.print("SQL#6 failed closing cursor.\n");
    System.out.println(e.toString());
    System.exit(0);
}

// We're done with the handle.
try {
    querySt.close();
} catch(SQLException e) {
    System.out.print("SQL#6 failed closing the handle.\n");
    System.out.println(e.toString());
    System.exit(0);
}

}
public void insert_purchase() {
    
    String            insert_purchase = "";     // The SQL text.
    PreparedStatement querySt   = null;   // The query handle.
    int         answers   = 0;   // A cursor.

       insert_purchase =
        "INSERT into yrb_purchase (cid, club, title, year, when, qnty)  VALUES('"+custID+"','"+club+"',?,'"+years[bookAnswers]+"', ?, '"+quantity+"')";                                                                                     

// Prepare the query.
try {
querySt = conDB.prepareStatement(insert_purchase);
 
} catch(SQLException e) {
System.out.println("SQL#7 failed in prepare");
System.out.println(e.toString());
System.exit(0);
}

// Execute the query.
try {
    querySt.setString(1, stringTitles[bookAnswers]);
    querySt.setTimestamp(2, ourJavaTimestampObject);
    answers = querySt.executeUpdate();
} catch(SQLException e) {
    System.out.println("SQL#7 failed in execute");
    System.out.println(e.toString());
    System.exit(0);
}
// We're done with the handle.
try {
querySt.close();
} catch(SQLException e) {
System.out.print("SQL#7 failed closing the handle.\n");
System.out.println(e.toString());
System.exit(0);
}

}
    public static void main(String[] args) {
        Yrbapp ct = new Yrbapp(args);
    }
}
