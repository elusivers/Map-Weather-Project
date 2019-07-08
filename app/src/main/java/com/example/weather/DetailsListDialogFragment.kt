package com.example.weather

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginStart
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.example.weather.model.Place
import com.google.android.gms.maps.model.LatLng
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import kotlinx.android.synthetic.main.fragment_details_list_dialog.*
import kotlinx.android.synthetic.main.fragment_details_list_dialog_item.view.*
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.lang.StringBuilder


// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"
const val COORDINATES = "coordinates"
val WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?"
//const val API_KEY_WEATHER = R.string.weather_api

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    DetailsListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [DetailsListDialogFragment.Listener].
 */
class DetailsListDialogFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null
    var temperature = 0
    var pressure = 0
    var wind = 0.0
    lateinit var picture: String
    lateinit var description: String
    lateinit var place: Place
    lateinit var pic: RequestBuilder<Drawable>
    //var parameters: DoubleArray = DoubleArray(3)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
        } else {
            //mListener = context as Listener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val latLng = arguments!!.getParcelable(COORDINATES) as LatLng
        val requestParam = RequestParams()
        requestParam.put("lat", latLng.latitude)
        requestParam.put("lon", latLng.longitude)
        //requestParam.put()
        //requestParam.add("appid", ""+ API_KEY_WEATHER)

        val http = StringBuilder()
        http.append(WEATHER_URL)
        http.append("lat=", latLng.latitude)
        http.append("&lon=", latLng.longitude)
        http.append("&appid=", resources.getString(R.string.weather_api))

        apiCall(http.toString())

    }

    fun apiCall(requestParams: String){
        //val connectivityManager = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        //val networkInfo = connectivityManager?.activeNetworkInfo
        //if(networkInfo != null && networkInfo.isConnected) {
            val asyncHttpClient = AsyncHttpClient()
            //asyncHttpClient.get()
            asyncHttpClient.get(requestParams, object : JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
                    super.onSuccess(statusCode, headers, response)
                    temperature = (response!!.getJSONObject("main").getDouble("temp") - 273.15).toInt()
                    pressure = response.getJSONObject("main").getDouble("pressure").toInt()
                    wind = response.getJSONObject("wind").getDouble("speed")
                    //picture = response.getJSONObject("weather").getJSONObject("0").getString("icon")
                    picture = response.getJSONArray("weather").getJSONObject(0).getString("icon")
                    description = response.getJSONArray("weather").getJSONObject(0).getString("description")
                    //description = response.getJSONObject("weather").getJSONObject("0").getString("description")

                    //parameters[0] = temperature
                    //parameters[1] = pressure
                    //parameters[2] = wind
                    place = Place(temperature, pressure, wind, description)

                    list.layoutManager = LinearLayoutManager(context)
                    list.adapter = DetailsAdapter(arguments!!.getInt(ARG_ITEM_COUNT),
                        arguments!!.getParcelable(COORDINATES))
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                    super.onFailure(statusCode, headers, throwable, errorResponse)
                    Log.d("error", "" + errorResponse)
                }
            })
        //}else
         //   Log.d("error", "no internet")
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun ondetailsClicked(position: Int)
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_details_list_dialog_item, parent, false)) {

        internal val text: TextView = itemView.text
        internal val arg: TextView = itemView.argument

        init {
            text.setOnClickListener {
                mListener?.let {
                    it.ondetailsClicked(adapterPosition)
                    dismiss()
                }
            }
        }
    }

    private inner class DetailsAdapter internal constructor(private val mItemCount: Int, private val mCoordinates: LatLng) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val args = resources.getStringArray(R.array.arguments)
            holder.text.text = args[position]
            when(position){
                0-> {holder.text.setTextColor(resources.getColor(R.color.colorPrimary))
                    holder.text.gravity = Gravity.CENTER
                    holder.text.text = place.description}
                1-> holder.arg.text = place.temperature.toString() + " \u2103"
                2-> holder.arg.text = place.pressure.toString() + " hPa"
                3-> holder.arg.text = place.wind.toString() + " m/s"
            }
        }

        override fun getItemCount(): Int {
            return mItemCount
        }
    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(itemCount: Int, coordinates:LatLng): DetailsListDialogFragment =
            DetailsListDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                    putParcelable(COORDINATES, coordinates)
                }
            }
    }
}
