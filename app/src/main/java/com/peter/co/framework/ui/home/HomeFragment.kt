package com.peter.co.framework.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.peter.co.R
import com.peter.co.databinding.FragmentHomeBinding
import com.peter.co.framework.ui.CopticOrphansViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: CopticOrphansViewModel by activityViewModels()
    private lateinit var adapter: RepositoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = RepositoryAdapter()
        binding.rvRepos.adapter = adapter
        binding.rvRepos.setHasFixedSize(false)
        binding.rvRepos.adapter = adapter.withLoadStateFooter(footer = RepositoryLoadStateAdapter())

        adapter.addLoadStateListener {
            if (it.refresh == LoadState.Loading)
                Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
        }
        lifecycleScope.launch {
            viewModel.listData.collect {
                Log.i("data", it.toString())
                adapter.submitData(it)

            }
            viewModel.listData.collectLatest {
                adapter.submitData(it)
            }
        }

    }


}