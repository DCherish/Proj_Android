package com.example.myminiproject ;

public class LikeListView
{
    private Double latitude ;
    private Double longitude ;
    private String location ;

    private Integer state ;

    public void setLatitude(Double a)
    {
        latitude = a ;
    }

    public void setLongitude(Double o)
    {
        longitude = o ;
    }

    public void setLocation(String l)
    {
        location = l;
    }

    public void setState(Integer s)
    {
        state = s ;
    }

    public Double getLatitude()
    {
        return this.latitude ;
    }

    public Double getLongitude()
    {
        return this.longitude ;
    }

    public String getLocation()
    {
        return this.location ;
    }

    public Integer getState()
    {
        return this.state ;
    }
}
