package com.example.weather.model

import android.net.Uri
import java.io.Serializable

data class Place(var temperature: Int, var pressure: Int, var wind: Double, var description: String):Serializable