package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.Stemoscope

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.cybermed.cdoc_patient.ViewModel.QRCodeData
import com.cybermed.cdoc_patient.ViewModel.QRCodeViewModel
import com.cybermed.cdoc_patient.databinding.FragmentQrcodeScannerBinding
//import kotlinx.android.synthetic.main.fragment_qrcode_scanner.view.*

/*
class QRCodeScannerFragment : Fragment() {

    private lateinit var codeScanner: CodeScanner
    private val qrCodeViewModel: QRCodeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_qrcode_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, view.scanner_view)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                qrCodeViewModel.setQRCodeData(QRCodeData(it.text, view))
            }
        }
        view.scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    companion object{
        val QRKEY = "QRKEY"
    }
}*/
class QRCodeScannerFragment : Fragment() {

    private lateinit var codeScanner: CodeScanner
    private val qrCodeViewModel: QRCodeViewModel by activityViewModels()
    private var _binding: FragmentQrcodeScannerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrcodeScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codeScanner = CodeScanner(requireActivity(), binding.scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                qrCodeViewModel.setQRCodeData(QRCodeData(it.text, binding.root))
            }
        }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val QRKEY = "QRKEY"
    }
}