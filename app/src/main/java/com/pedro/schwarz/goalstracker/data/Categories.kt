package com.pedro.schwarz.goalstracker.data

import android.content.Context
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.model.Category

fun getCategories(context: Context): List<Category> {
    return listOf(
        Category(
            id = 1,
            icon = R.drawable.ic_fitness,
            color = R.color.colorFitness,
            title = context.getString(R.string.fitness_label)
        ), Category(
            id = 2,
            icon = R.drawable.ic_work,
            color = R.color.colorWork,
            title = context.getString(R.string.work_label)
        ), Category(
            id = 3,
            icon = R.drawable.ic_school,
            color = R.color.colorSchool,
            title = context.getString(R.string.school_label)
        ), Category(
            id = 4,
            icon = R.drawable.ic_social,
            color = R.color.colorSocial,
            title = context.getString(R.string.social_label)
        ), Category(
            id = 5,
            icon = R.drawable.ic_love,
            color = R.color.colorLove,
            title = context.getString(R.string.love_label)
        ), Category(
            id = 6,
            icon = R.drawable.ic_travel,
            color = R.color.colorTravel,
            title = context.getString(R.string.travel_label)
        ), Category(
            id = 7,
            icon = R.drawable.ic_purchase,
            color = R.color.colorPurchase,
            title = context.getString(R.string.purchase_label)
        ),
        Category(
            id = 8,
            icon = R.drawable.ic_financial,
            color = R.color.colorFinancial,
            title = context.getString(R.string.financial_label)
        )
    )
}