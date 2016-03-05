package org.kamusi.kamusifidgetapp;

import java.util.ArrayList;

public class Globals {

	   private static Globals instance;
	 
	   // Global variable
	   private Boolean login=true;
	   private String username="Guest";
	   private Boolean next=false;
	   private Boolean profile=false;
	   private ArrayList<String> words = new ArrayList<String>();
	   private ArrayList<String> usedwords = new ArrayList<String>();
	   
	   // Restrict the constructor from being instantiated
	   private Globals(){}
	   
	   public Boolean getNext()
	   {
		   return this.next;
	   }
	   
	   public void setNext(Boolean d)
	   {
		   this.next = d;
	   }
	   
	   public Boolean getProfile()
	   {
		   return this.profile;
	   }
	   
	   public void setProfile(Boolean d)
	   {
		   this.profile = d;
	   }
	   
	   public Boolean getLogin(){
		     return this.login;
	   }
	 
	   public void setLogin(Boolean d){
	     this.login=d;
	   }
	   
	   public String getUsername(){
	     return this.username;
	   }
	   
	   public void setUsername(String username){
		     this.username = username;
	   }
	   
	  	 
	   public static synchronized Globals getInstance(){
	     if(instance==null){
	       instance=new Globals();
	     }
	     return instance;
	   }
	   
	}