export default function StatusBadge({ status }: { status: string }) {

  const colors: Record<string, string> = {

    PENDING: "bg-yellow-100 text-yellow-700",
    ACCEPTED: "bg-blue-100 text-blue-700",
    ON_PROGRESS: "bg-purple-100 text-purple-700",
    COMPLETED: "bg-green-100 text-green-700",
    CANCELLED: "bg-red-100 text-red-700",

  };

  return (

    <span className={`px-2 py-1 text-xs rounded ${colors[status]}`}>
      {status}
    </span>

  );
}