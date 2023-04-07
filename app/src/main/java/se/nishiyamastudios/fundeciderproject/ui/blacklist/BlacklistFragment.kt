package se.nishiyamastudios.fundeciderproject.ui.blacklist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import se.nishiyamastudios.fundeciderproject.utilityclass.FirebaseUtility
import se.nishiyamastudios.fundeciderproject.databinding.FragmentBlacklistBinding
import se.nishiyamastudios.fundeciderproject.dataclass.BlackListObject
import se.nishiyamastudios.fundeciderproject.ui.login.LoginViewModel

class BlacklistFragment : Fragment() {

    var _binding : FragmentBlacklistBinding? = null
    val binding get() = _binding!!

    val viewModel by viewModels<LoginViewModel>()
    val fbUtil = FirebaseUtility()

    private var blacklistadapter = BlacklistAdapter()

    companion object {
        fun newInstance() = BlacklistFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        blacklistadapter.frag = this

        _binding = FragmentBlacklistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.blacklistRV.adapter = blacklistadapter
        binding.blacklistRV.layoutManager = LinearLayoutManager(requireContext())

        val blacklistObserver = Observer<List<BlackListObject>> {
            blacklistadapter.notifyDataSetChanged()
        }

        fbUtil.blacklistPlaces.observe(viewLifecycleOwner, blacklistObserver)

        fbUtil.loadBlacklist()

    }

}