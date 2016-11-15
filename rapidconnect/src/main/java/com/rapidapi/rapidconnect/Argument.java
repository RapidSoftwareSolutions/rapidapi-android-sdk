package com.rapidapi.rapidconnect;

public class Argument {

  private String type;
  private String value;

  public Argument(String type, String value) {
    this.type = type;
    this.value = value;
  }

  public final String getType(){
    return type;
  }

  public final String getValue(){
    return value;
  }

}