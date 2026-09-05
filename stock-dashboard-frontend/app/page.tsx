import Link from "next/link";

export default function Home() {
  return (
    <main className="min-h-screen bg-background pt-navbar text-on-surface">
      <section className="mx-auto max-w-7xl px-6 py-24 md:px-16">
        <p className="mb-4 font-mono text-xs font-semibold tracking-[0.18em] text-primary">
          BUILT FOR PRECISION ANALYSIS
        </p>
        <h1 className="max-w-4xl text-5xl font-semibold tracking-tight md:text-7xl">
          Discover a dashboard for <span className="text-on-surface-variant">screening, AI news and AI analysis</span>
        </h1>
        <p className="mt-6 max-w-2xl text-lg text-on-surface-variant">
          A ease of life tool, to help you screen stocks, get AI summaries about the latest news.
        </p>
        <div className="mt-10 flex flex-wrap gap-4">
          <Link
            href="/ticker"
            className="bg-primary px-8 py-4 font-bold text-on-primary transition-all hover:bg-secondary"
          >
            Get Started <span>📊</span>
          </Link>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-6 pb-24 md:px-16" id="features">
        <div className="mb-12">
          <h2 className="text-4xl font-semibold tracking-tight md:text-5xl">Engineered for ease of life</h2>
        </div>
        <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
          <div className="border border-outline-variant bg-white p-10 shadow-sm">
            <div className="mb-8 flex h-12 w-12 items-center justify-center border border-outline-variant bg-slate-50 text-2xl text-primary">🔍</div>
            <h3 className="mb-4 text-xl font-medium">Stock Search</h3>
            <p className="text-on-surface-variant">Search for stocks.</p>
          </div>
          <div className="border border-outline-variant bg-white p-10 shadow-sm">
            <div className="mb-8 flex h-12 w-12 items-center justify-center border border-outline-variant bg-slate-50 text-2xl text-primary">◈</div>
            <h3 className="mb-4 text-xl font-medium">Stock Screening</h3>
            <p className="text-on-surface-variant">Screen your stocks based on many parameters</p>
          </div>
          <div className="border border-outline-variant bg-white p-10 shadow-sm">
            <div className="mb-8 flex h-12 w-12 items-center justify-center border border-outline-variant bg-slate-50 text-2xl text-primary">✦</div>
            <h3 className="mb-4 text-xl font-medium">Ai features</h3>
            <p className="text-on-surface-variant">
              Deep-dive intelligence on any subject. Our AI parses filings, transcripts, and sentiment automatically.
            </p>
          </div>
        </div>
      </section>
    </main>
  );
}
