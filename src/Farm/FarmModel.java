package Farm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import Controller.DatabaseManager;

public class FarmModel {


    private int userSeedQuantity;
	
	
    
        public static class Crop {
            private String type;
            private int quantity;
            private int growthTime;

            public Crop(String type, int quantity, int growthTime) {
                this.type = type;
                this.quantity = quantity;
                this.growthTime = growthTime ;
                
            }

            public String getType() {
                return type;
            }

            public int getQuantity() {
                return quantity;
            }

            public int getGrowthTime() {
                return growthTime;
            }
            
           

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }


            public void setGrowthTime(int growthTime) {
                this.growthTime = growthTime;
            }
            
        }
        
        public static class User {
            private String id;
            private int money;

            public User(String id, int money) {
                this.id = id;
                this.money = money;
            }

            // methods to deduct money and get money
            public void deductMoney(int amount) {
                this.money -= amount;
            }

            public int getMoney() {
                return this.money;
            }
            
            public String getId() {
                return id;
            }
        }
    }

    
    
