package com.miro.Laivanupotus.utils;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.miro.Laivanupotus.model.Ship.ShipType;

public class ShipTypeDeserializer extends JsonDeserializer<ShipType>{

	@Override
	public ShipType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		int value = p.getValueAsInt();
		return Arrays.stream(ShipType.values())
				.filter(type -> type.getLength() == value)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid value: " + value));
		
	}
	

}
