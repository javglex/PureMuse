package com.newpath.puremuse.models


/**
 * Keeps track of everything that makes a music file. Name, album, path etc.
 */
class AudioFileModel {

    var _id: String
    var artist: String
    var title: String
    var album: String
    var data: String
    var displayName: String
    var duration: String
    var path:String
    var onDevice:Boolean

    constructor(_id: String, artist: String,title: String, data: String,displayName: String,duration: String, album:String, path:String, onDevice:Boolean) {
        this._id = _id
        this.artist = artist
        this.title = title
        this.data = data
        this.displayName = displayName
        this.duration = duration
        this.album = album
        this.path = path
        this.onDevice = onDevice;
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
        this.onDevice = false;
    }

    fun isOnDevice(onDevice: Boolean){
        this.onDevice = onDevice
    }

    override fun toString(): String {
        return "id: "+ _id + " artist " + artist + " title " + " album " + album + "data: "+ data + " displayName "+ displayName  + " duration " + duration + " path: " + path
    }

    fun toHashableString(): String{
        return artist+album+duration+displayName;
    }

    fun toSearchableString():String{
        var tempSearchable: String = album+artist+displayName;
        return tempSearchable.toLowerCase();
    }
}