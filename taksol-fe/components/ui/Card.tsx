export default function Card({ title, children }: any) {

  return (

    <div className="bg-card p-6 rounded-xl shadow-sm border border-gray-200">

      <h2 className="text-lg font-semibold mb-3 text-textMain">
        {title}
      </h2>

      <div className="text-textSoft">
        {children}
      </div>

    </div>

  );

}