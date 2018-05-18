package com.newpath.puremuse.models

class AudioFileModel {

    var _id: String
    var artist: String
    var title: String
    var album: String
    var data: String
    var displayName: String
    var duration: String
    var path:String

    constructor(_id: String, artist: String,title: String, data: String,displayName: String,duration: String, album:String, path:String) {
        this._id = _id
        this.artist = artist
        this.title = title
        this.data = data
        this.displayName = displayName
        this.duration = duration
        this.album = album
        this.path = path
    }

    constructor(){
        this._id = ""
        this.artist = "n/a"
        this.title = "n/a"
        this.data = "n/a"
        this.displayName = "n/a"
        this.duration = "n/a"
        this.album = "n/a"
        this.path = "n/a"
    }

    override fun toString(): String {
        return "id: "+ _id + " artist " + artist + " title " + " album " + album + "data: "+ data + " displayName "+ displayName  + " duration " + duration + " path: " + path
    }

    fun toSearchableString():String{
        var tempSearchable: String = album+artist+displayName;
        return tempSearchable.toLowerCase();
    }
}