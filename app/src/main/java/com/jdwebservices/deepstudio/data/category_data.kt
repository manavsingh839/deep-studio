package com.jdwebservices.deepstudio.data


class category_data{

    var category_id:Int
    var categoryName: String
    var categoryicon:String
    var categoryDescription:String

    constructor(
        category_id:Int,
        categoryName:String,
        categoryicon:String,
        categoryDescription: String)
    {
        this.category_id =category_id
        this.categoryName = categoryName
        this.categoryicon =categoryicon
        this.categoryDescription =categoryDescription
    }

}