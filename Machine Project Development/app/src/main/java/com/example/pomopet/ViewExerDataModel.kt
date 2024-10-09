package com.example.pomopet

//This file specifies the parameters that the ArrayList<ViewExerDataModel> will have
class ViewExerDataModel (exerName: String, exerIcon: Int){
    var exerName = exerName
        private set
    var exerIcon = exerIcon
        private set

    override fun toString(): String {
        return "com.example.pomopet.ViewExerDataModel{" +
                "exerName='" + exerName + '\'' +
                ", exerIcon='" + exerIcon + '\'' +
                '}'
    }
}