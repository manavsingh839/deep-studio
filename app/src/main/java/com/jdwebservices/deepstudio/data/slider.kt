package com.jdwebservices.data

class slider{

    var slider_image_id:Int
    var slider_image: String
    var category:String

    constructor(
        slider_image_id:Int,
        slider_image:String,
        category: String)
    {
        this.slider_image_id =slider_image_id
        this.slider_image = slider_image
        this.category =category
    }

}