"use client";

import { useState, useRef } from "react";
import { GoogleMap, Marker, Autocomplete, useJsApiLoader } from "@react-google-maps/api";

const containerStyle = {
  width: "100%",
  height: "400px",
};

const defaultCenter = {
  lat: -8.670458,
  lng: 115.212629,
};

interface Props {
  onSelect: (
    type: "origin" | "destination",
    lat: number,
    lng: number,
    address: string
  ) => void;
}

export default function MapPicker({ onSelect }: Props) {
  // Load Google Maps API only once
  const { isLoaded, loadError } = useJsApiLoader({
    googleMapsApiKey: process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY!,
    libraries: ["places"],
  });

  const [map, setMap] = useState<google.maps.Map | null>(null);
  const [originMarker, setOriginMarker] = useState<google.maps.LatLngLiteral | null>(null);
  const [destinationMarker, setDestinationMarker] = useState<google.maps.LatLngLiteral | null>(null);

  const originRef = useRef<google.maps.places.Autocomplete | null>(null);
  const destinationRef = useRef<google.maps.places.Autocomplete | null>(null);

  const handlePlaceSelect = (
    type: "origin" | "destination",
    autocomplete: google.maps.places.Autocomplete | null
  ) => {
    if (!autocomplete) return;

    const place = autocomplete.getPlace();

    if (!place || !place.geometry || !place.geometry.location) {
      console.warn("Place tidak valid atau belum dipilih dari dropdown");
      return;
    }

    const lat = place.geometry.location.lat();
    const lng = place.geometry.location.lng();
    const address = place.formatted_address || place.name || "";

    const position = { lat, lng };

    if (type === "origin") setOriginMarker(position);
    else setDestinationMarker(position);

    map?.panTo(position);

    onSelect(type, lat, lng, address);
  };

  if (loadError) return <p>Gagal load Google Maps</p>;
  if (!isLoaded) return <p>Loading Map...</p>;

  return (
    <div className="space-y-4">
      {/* INPUT JEMPUT */}
      <Autocomplete
        onLoad={(auto) => (originRef.current = auto)}
        onPlaceChanged={() => handlePlaceSelect("origin", originRef.current)}
        options={{ fields: ["formatted_address", "geometry", "name"] }}
      >
        <input
          type="text"
          placeholder="Lokasi Jemput"
          className="w-full border p-3 rounded"
        />
      </Autocomplete>

      {/* INPUT TUJUAN */}
      <Autocomplete
        onLoad={(auto) => (destinationRef.current = auto)}
        onPlaceChanged={() => handlePlaceSelect("destination", destinationRef.current)}
        options={{ fields: ["formatted_address", "geometry", "name"] }}
      >
        <input
          type="text"
          placeholder="Lokasi Tujuan"
          className="w-full border p-3 rounded"
        />
      </Autocomplete>

      {/* MAP */}
      <GoogleMap
        mapContainerStyle={containerStyle}
        center={defaultCenter}
        zoom={13}
        onLoad={(mapInstance) => setMap(mapInstance)}
      >
        {originMarker && <Marker position={originMarker} />}
        {destinationMarker && <Marker position={destinationMarker} />}
      </GoogleMap>
    </div>
  );
}