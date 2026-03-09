"use client";

import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
} from "recharts";

const data = [
  { day: "Mon", earnings: 200000 },
  { day: "Tue", earnings: 350000 },
  { day: "Wed", earnings: 250000 },
  { day: "Thu", earnings: 500000 },
  { day: "Fri", earnings: 450000 },
];

export default function EarningsChart() {
  return (
    <div className="bg-white p-5 rounded shadow">

      <h2 className="font-semibold mb-4">
        Earnings Analytics
      </h2>

      <LineChart width={600} height={300} data={data}>

        <CartesianGrid strokeDasharray="3 3" />

        <XAxis dataKey="day" />

        <YAxis />

        <Tooltip />

        <Line
          type="monotone"
          dataKey="earnings"
          stroke="#38bdf8"
          strokeWidth={3}
        />

      </LineChart>

    </div>
  );
}