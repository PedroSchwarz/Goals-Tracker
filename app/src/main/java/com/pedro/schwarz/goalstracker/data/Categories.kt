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
            title = "Fitness"
        ), Category(
            id = 2,
            icon = R.drawable.ic_work,
            color = R.color.colorWork,
            title = "Work"
        ), Category(
            id = 3,
            icon = R.drawable.ic_school,
            color = R.color.colorSchool,
            title = "School"
        ), Category(
            id = 4,
            icon = R.drawable.ic_social,
            color = R.color.colorSocial,
            title = "Social life"
        ), Category(
            id = 5,
            icon = R.drawable.ic_love,
            color = R.color.colorLove,
            title = "Love life"
        ), Category(
            id = 6,
            icon = R.drawable.ic_travel,
            color = R.color.colorTravel,
            title = "Travel"
        ), Category(
            id = 7,
            icon = R.drawable.ic_purchase,
            color = R.color.colorPurchase,
            title = "Purchase"
        ),
        Category(
            id = 8,
            icon = R.drawable.ic_financial,
            color = R.color.colorFinancial,
            title = "Financial"
        )
    )
}