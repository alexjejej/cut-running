package com.cut.android.running.usecases.gestioncarreras.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cut.android.running.R
import com.cut.android.running.models.User

class UserAdapter: RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var users = listOf<User>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_user_list, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount() = users.size

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lblUserName: TextView = view.findViewById(R.id.lblUserName)
        val lblUserEmail: TextView = view.findViewById(R.id.lblUserEmail)

        fun bind(user: User) {
            lblUserName.text = "${user.firstname} ${user.lastname}"
            lblUserEmail.text = user.email
        }
    }
}