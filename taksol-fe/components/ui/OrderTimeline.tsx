export default function OrderTimeline({ status }: any) {

  const steps = [
    "PENDING",
    "ACCEPTED",
    "ON_PROGRESS",
    "COMPLETED",
  ];

  return (

    <div className="flex gap-4">

      {steps.map((s) => (

        <div key={s} className="flex flex-col items-center">

          <div
            className={`w-4 h-4 rounded-full ${
              status === s ? "bg-primary" : "bg-gray-300"
            }`}
          />

          <p className="text-xs">{s}</p>

        </div>

      ))}

    </div>

  );
}