import { ReactNode } from "react";

interface Props {
  title: string;
  value: string | number;
  icon: ReactNode;
}

export default function StatsCard({ title, value, icon }: Props) {
  return (
    <div className="bg-white p-5 rounded-lg shadow flex justify-between items-center">

      <div>
        <p className="text-sm text-gray-500">{title}</p>
        <h2 className="text-2xl font-bold">{value}</h2>
      </div>

      <div className="text-primary text-3xl">
        {icon}
      </div>

    </div>
  );
}