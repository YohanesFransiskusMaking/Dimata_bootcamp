export default function Modal({
  title,
  children,
  onClose,
}: any) {

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center">

      <div className="bg-white p-6 rounded w-96">

        <h2 className="font-bold mb-4">
          {title}
        </h2>

        {children}

        <button
          onClick={onClose}
          className="mt-4 text-red-500"
        >
          Close
        </button>

      </div>

    </div>
  );
}