"use client";

import { useState } from "react";

export default function AdvancedTable({
  columns,
  data,
}: any) {

  const [search, setSearch] = useState("");

  const filtered = data.filter((row: any) =>
    JSON.stringify(row).toLowerCase().includes(search.toLowerCase())
  );

  return (

    <div className="space-y-4">

      <input
        placeholder="Search..."
        className="border p-2 rounded"
        onChange={(e) => setSearch(e.target.value)}
      />

      <table className="w-full bg-white shadow rounded">

        <thead className="bg-primary text-white">

          <tr>
            {columns.map((c: string) => (
              <th key={c} className="p-2 text-left">
                {c}
              </th>
            ))}
          </tr>

        </thead>

        <tbody>

          {filtered.map((row: any, i: number) => (

            <tr key={i} className="border-t">

              {columns.map((c: string) => (
                <td key={c} className="p-2">
                  {row[c]}
                </td>
              ))}

            </tr>

          ))}

        </tbody>

      </table>

    </div>

  );
}