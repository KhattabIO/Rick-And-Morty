package com.slayer.rickandmorty.core

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.facebook.shimmer.ShimmerFrameLayout
import com.slayer.rickandmorty.R
import com.slayer.rickandmorty.databinding.DialogNoInternetBinding

fun Any?.printToLog(tag: String = "DEBUG_LOG") {
    Log.d(tag, toString())
}

fun View.gone() = run { visibility = View.GONE }

fun View.visible() = run { visibility = View.VISIBLE }

fun View.invisible() = run { visibility = View.INVISIBLE }

infix fun ShimmerFrameLayout.startShimmerIf(condition: Boolean) = run {
    if (condition) startShimmer() else stopShimmer()
}

infix fun View.visibleIf(condition: Boolean) =
    run { visibility = if (condition) View.VISIBLE else View.GONE }

infix fun View.goneIf(condition: Boolean) =
    run { visibility = if (condition) View.GONE else View.VISIBLE }

infix fun View.invisibleIf(condition: Boolean) =
    run { visibility = if (condition) View.INVISIBLE else View.VISIBLE }

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.toast(@StringRes message: Int) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.toast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun safeCall(context: Context, call: () -> Unit) {
    if (isNetworkConnected(context)) {
        call.invoke()
    }
    else {
        showNoInternetDialog(context)
    }
}

private fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val activeNetwork =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

    return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

private fun showNoInternetDialog(context: Context) {
    val dialogNoInternetBinding = DialogNoInternetBinding.inflate(LayoutInflater.from(context))
    val dialog = DefaultDialog(context, dialogNoInternetBinding.root)

    dialogNoInternetBinding.btnConfirm.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
}

fun createSpannableString(start: Int, end: Int, text: String,context: Context): SpannableString {
    val spannableString = SpannableString(text)

    // Apply a different color to the specific word
    spannableString.setSpan(
        ForegroundColorSpan(
            ContextCompat.getColor(
                context,
                R.color.md_theme_light_primary
            )
        ),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    return spannableString
}