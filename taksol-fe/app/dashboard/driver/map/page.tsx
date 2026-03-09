"use client";

import { GoogleMap, Marker, useJsApiLoader } from "@react-google-maps/api";

export default function DriverMap(){

  const { isLoaded } = useJsApiLoader({
    googleMapsApiKey: process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY!,
  });

  if (!isLoaded) return <p>Loading map...</p>;

  return(

    <GoogleMap
      mapContainerStyle={{ width:"100%", height:"500px"}}
      center={{ lat:-8.65, lng:115.22 }}
      zoom={12}
    >

      <Marker position={{ lat:-8.66, lng:115.21 }} />

    </GoogleMap>

  );
}