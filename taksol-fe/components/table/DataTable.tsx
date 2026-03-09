"use client";

interface Props {
  columns: string[];
  data: any[];
}

export default function DataTable({
  columns,
  data,
}: Props) {

  return (
    <table className="w-full bg-white shadow rounded">

      <thead className="bg-primary text-white">

        <tr>
          {columns.map((c) => (
            <th key={c} className="p-2 text-left">
              {c}
            </th>
          ))}
        </tr>

      </thead>

      <tbody>

        {data.map((row, i) => (

          <tr key={i} className="border-t">

            {columns.map((c) => (
              <td key={c} className="p-2">
                {row[c]}
              </td>
            ))}

          </tr>

        ))}

      </tbody>

    </table>
  );
}