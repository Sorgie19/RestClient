package edu.uark.rtsorge.restclient;


import com.google.gson.annotations.SerializedName;

//COMPANY POJO (APART OF USER)
public class Company{

	@SerializedName("bs")
	private String bs;

	@SerializedName("catchPhrase")
	private String catchPhrase;

	@SerializedName("name")
	private String name;

	public void setBs(String bs){
		this.bs = bs;
	}

	public String getBs(){
		return bs;
	}

	public void setCatchPhrase(String catchPhrase){
		this.catchPhrase = catchPhrase;
	}

	public String getCatchPhrase(){
		return catchPhrase;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	@Override
 	public String toString(){
		return 
			"Company{" + 
			"bs = '" + bs + '\'' + 
			",catchPhrase = '" + catchPhrase + '\'' + 
			",name = '" + name + '\'' + 
			"}";
		}
}