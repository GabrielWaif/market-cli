package com.market.cashier;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.Random;
import org.bson.Document;
import org.bson.conversions.Bson;

public class Products {

  //here you can set your local mongodb uri - the default is "mongodb://localhost:27017"
  private String uri = "mongodb://localhost:27017";
  private MongoClient client;
  private MongoDatabase database;
  private MongoCollection<Document> products;

  //Initializes the data base and connects to 27017 -> Market -> Products.
  Products() {
    try {
      client = MongoClients.create(uri);
      database = client.getDatabase("Market");
      products = database.getCollection("Products");
      System.out.println("Connected to database...");
    } catch (Error err) {
      System.err.println(err);
    }
  }

  //Shows all products that are in the database - sorted by id
  public void showProducts() {
    System.out.println("----------------");
    System.out.println("Products:");
    System.out.println("----------------");
    Bson filter = Filters.eq("id", 1);
    MongoCursor cursor = this.products.find().sort(filter).iterator();
    try {
      while (cursor.hasNext()) {
        Document currentItem = ((Document) cursor.next());
        System.out.println(
          currentItem.get("id") +
          ") " +
          currentItem.get("ammount") + " / " + currentItem.get("name") +
          " - $" +
          currentItem.get("price")
        );
      }
    } finally {
      cursor.close();
      System.out.println("----------------");
    }
  }

  //adds a product to the database based on the parametters given
  public boolean addProduct(String name, String ammount, double price) {
    if (price > 0) {
      try {
        Document newProduct = new Document("id", newUniqueId())
          .append("name", name)
          .append("ammount", ammount)
          .append("price", price);
        this.products.insertOne(newProduct);
        System.out.println(name + " was successfully added to the database.");
        return true;
      } catch (Error err) {
        System.err.println(err + " - something wrong happened");
        return false;
      }
    } else {
      System.out.println("Price is too low! choose a higher one.");
      return false;
    }
  }

  //removes the product from the database based from the id given
  public boolean removeProduct(int id) {
    if (this.hasId(id)) {
      try {
        Bson filter = Filters.eq("id", id);
        this.products.deleteOne(filter);
        return true;
      } catch (Error err) {
        System.err.println(err);
        return false;
      }
    } else {
      System.out.println("Id doesn't exist! please enter a valid one.");
      return false;
    }
  }

  // returns a boolean saying if the informed id exists in the database or not
  public boolean hasId(int id) {
    try {
      Bson filter = Filters.eq("id", id);
      MongoCursor cursor = this.products.find(filter).iterator();
      return cursor.hasNext();
    } catch (Error err) {
      System.err.println(err);
      return false;
    }
  }

  // Returns an id that doesn't exist in the datatbase from 1 to 100
  private int newUniqueId() {
    Random r = new Random();
    int newId = r.nextInt(100) + 1;
    if (this.hasId(newId)) return newUniqueId(); else return newId;
  }

  public Document getOneInfo(int id){
      Bson filter = Filters.eq("id", id);
      MongoCursor cursor = this.products.find(filter).iterator();
      return (Document)cursor.next();
  }
}