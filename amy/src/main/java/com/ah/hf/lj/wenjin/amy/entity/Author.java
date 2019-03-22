package com.ah.hf.lj.wenjin.amy.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Author {

	private int id;
	@Value("${author.name}")
	private String name;
	@Value("${author.value}")
	private String strValue;
	@Value("${author.bignumber}")
	private long bigIntValue;
	@Value("${author.number}")
	private int intValue;
	@Value("${author.range2}")
	private int rangeIntValue;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public long getBigIntValue() {
		return bigIntValue;
	}

	public void setBigIntValue(long bigIntValue) {
		this.bigIntValue = bigIntValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public int getRangeIntValue() {
		return rangeIntValue;
	}

	public void setRangeIntValue(int rangeIntValue) {
		this.rangeIntValue = rangeIntValue;
	}

}
