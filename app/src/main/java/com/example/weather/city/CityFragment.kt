package com.example.weather.city

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.App
import com.example.weather.Database
import com.example.weather.R

class CityFragment : Fragment(), CityAdapter.CityItemListener {

    private val TAG = CityFragment::class.java.simpleName

    interface CityFragmentListener {
        fun onCitySelected(city: City)
        fun onSelectionCleared()
        fun onEmptyCities()
    }
    var listener: CityFragmentListener? = null

    private lateinit var cities: MutableList<City>
    private lateinit var database: Database
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = App.database
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_city, container, false)
        recyclerView = view.findViewById(R.id.cities_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cities = database.getAllCities()
        Log.i(TAG, "cities $cities")

        adapter = CityAdapter (cities, this)
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_city, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            R.id.action_create_city -> {
                showCreateCityDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCreateCityDialog() {
        val createCityFragment = CreateCityDialogFragment()
        createCityFragment.listener = object : CreateCityDialogFragment.CreateCityDialogListener{
            override fun onDialogPositiveClick(cityName: String) {
                saveCity(City(cityName))
            }

            override fun onDialogNegativeClick() {

            }

        }
        createCityFragment.show(requireFragmentManager(), "CreateCityDialogFragment")
    }

    private fun saveCity(city: City) {
        if(database.createCity(city)) {
            cities.add(city)
            adapter.notifyDataSetChanged()
        } else {
            Toast.makeText(
                context,
                getString(R.string.city_message_error_could_not_create_city),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCitySelected(city: City) {
    listener?.onCitySelected(city)
    }

    override fun onCityDeleted(city: City) {
        showDeleteCityDialog(city)
    }

    fun selectFirstCity() {
        when(cities.isEmpty()) {
        true -> listener?.onEmptyCities()
            false -> onCitySelected(cities.first())
        }
    }

    private fun showDeleteCityDialog(city: City) {
        val deleteCityFragment = DeleteCityDialogFragment.newInstance(city.name)
        deleteCityFragment.listener = object: DeleteCityDialogFragment.DeleteCityDialogListener{
            override fun onDialogPositiveClick() {
                deleteCity(city)
            }

            override fun onDialogNegativeClick() { }

        }
        deleteCityFragment.show(requireFragmentManager(), "DeleteCityDialogFragment")
    }

    private fun deleteCity(city: City) {
    if (database.deleteCity(city)) {
        cities.remove(city)
        adapter.notifyDataSetChanged()
        selectFirstCity()
        Toast.makeText(
            context,
            getString(R.string.city_message_info_city_deleted, city.name),
            Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(
            context,
            getString(R.string.city_message_error_delete_city, city.name),
            Toast.LENGTH_SHORT
        ).show()
        }
    }
}