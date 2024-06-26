package com.slayer.rickandmorty.ui.fragments.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.slayer.common.goneIf
import com.slayer.common.hideKeyboard
import com.slayer.common.visibleIf
import com.slayer.rickandmorty.R
import com.slayer.rickandmorty.databinding.FragmentCharactersBinding
import com.slayer.rickandmorty.epoxy.controllers.CharactersController
import com.slayer.rickandmorty.ui.dialogs.CustomerFilterDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CharactersFragment : Fragment() {
    private var _binding: FragmentCharactersBinding? = null
    private val binding get() = _binding!!

    private val vm: CharactersViewModel by viewModels()

    private val TAG = this::class.simpleName

    private val charactersController by lazy {
        CharactersController {
            if (it.isFavorite) {
                vm.insertCharacterToFav(it)
            } else {
                vm.deleteCharacterToFav(it)
            }
        }
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigateToLoginIfNotAuthenticated()
    }

    private fun navigateToLoginIfNotAuthenticated() {
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            findNavController().navigate(R.id.action_charactersFragment_to_auth_graph)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharactersBinding.inflate(inflater, container, false)

        vm.getRandomCharacters()

        setupRecyclerView()

        observeCustomersPagingData()
        observeCustomersPagingData()
        observeRandomCharacters()
        observeAdapterLoadingState()

        handleSearchInputEndIconClick()
        handleKeyboardSearchBtnClick()
        handleFilterBtnClick()

        binding.fab.setOnClickListener {
            binding.rvCharacters.startNestedScroll(
                ViewCompat.SCROLL_AXIS_VERTICAL,
                ViewCompat.TYPE_NON_TOUCH
            )
            binding.rvCharacters.smoothScrollToPosition(0)
        }

        binding.rvCharacters.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItemPosition == 0 && dy > 0) {
                    binding.fab.hide()
                } else if (firstVisibleItemPosition > 3 && dy < 0) {
                    binding.fab.show()
                } else {
                    binding.fab.hide()
                }
            }
        })


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun observeRandomCharacters() {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.randomCharactersResult.collectLatest {
                it?.let {
                    charactersController.randomCharss.addAll(it)
                    charactersController.requestModelBuild()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun handleFilterBtnClick() {
        binding.btnFilter.apply {
            setOnClickListener {
                isClickable = false

                val dialog = CustomerFilterDialog {
                    isClickable = true
                }

                dialog.show(childFragmentManager, dialog.javaClass.simpleName)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            val layoutManager = GridLayoutManager(requireContext(), 2)

            rvCharacters.adapter = charactersController.adapter
            rvCharacters.layoutManager = layoutManager

            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position in 0..2) 2 else 1
                }
            }
        }
    }

    private fun observeCustomersPagingData() {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.characterFlow.collectLatest {
                charactersController.submitData(it)
            }
        }
    }

    private fun handleSearchInputEndIconClick() {
        binding.containerSearch.apply {
            setEndIconOnClickListener {
                if (editText?.text.isNullOrEmpty()) {
                    hideKeyboard()
                    editText?.clearFocus()
                } else {
                    editText?.text = null
                    vm.submitQuery(null, vm.getCurrentStatus(), vm.getCurrentGender())
                }
            }
        }
    }

    private fun handleKeyboardSearchBtnClick() {
        binding.containerSearch.editText?.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                vm.submitQuery(v.text.toString(), vm.getCurrentStatus(), vm.getCurrentGender())

                hideKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun observeAdapterLoadingState() {
        charactersController.addLoadStateListener { loadState ->
            val isRefreshing = loadState.refresh is LoadState.Loading
            binding.apply {
                if (isRefreshing) {
                    shimmerLayout.shimmerLayout.startShimmer()
                } else {
                    shimmerLayout.shimmerLayout.stopShimmer()
                }

                shimmerLayout.shimmerLayout.apply {
                    this visibleIf isRefreshing
                }

                layoutGroup goneIf isRefreshing
            }
        }
    }
}