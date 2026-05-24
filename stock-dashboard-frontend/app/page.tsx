import Link from "next/link";
import Footer from "./components/Footer";

export default function Home() {
  return (
    <main className="min-h-screen bg-background text-on-surface pt-navbar">
      {/* Hero Section */}
      <section className="relative overflow-hidden px-4 md:px-16 py-24 md:py-32">
        <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-12 gap-6 items-center">
          <div className="md:col-span-6 space-y-10">
            <div className="inline-flex items-center gap-2 px-3 py-1 bg-surface-container-high border border-outline-variant rounded-full text-on-surface text-xs uppercase tracking-widest">
              <span className="text-[14px]">⚡</span>
              BUILT FOR EASE OF LIFE
            </div>
            <h1 className="text-4xl md:text-5xl font-semibold leading-tight text-primary">
              Discover a dashboard for <br />
              <span className="text-on-surface-variant">screening, AI news and AI analysis</span>
            </h1>
            <p className="text-lg text-on-surface-variant max-w-lg leading-relaxed">
              A ease of life tool, to help you screen stocks, get AI summaries about the latest news.
            </p>
            <div className="flex flex-wrap gap-4 pt-6">
              {/* TODO: Fix the link to ticker search when the page is ready */}
              <Link
                href="/ticker"
                className="px-8 py-4 bg-primary text-on-primary font-bold rounded-lg hover:bg-secondary-fixed transition-all flex items-center gap-2"
              >
                Get Started
                <span>📊</span>
              </Link>
            </div>
          </div>
          {/* Visual Element */}
          <div className="md:col-span-6 relative h-[400px] md:h-[600px] flex items-center justify-end">
            <div className="absolute right-0 w-[110%] h-[400px] md:h-[500px] bg-surface-container-low rounded-3xl overflow-hidden border border-outline-variant grayscale opacity-60">
              <img
                className="w-full h-full object-cover mix-blend-luminosity"
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuAR01nHzy3yH3NwRRsB8QkkFI1m7sL7q3tKavR_I4epgRw-8Ed2c_UY5QbbZSb4Omz9lpm4m0ZqHyNXJJVEZNH-c8NgdA_MBoCaD7An6Wt6acwpagOuIZfgWyHBKZdplo0U8bdxIasndHbYyhxmF5pWbz6WE2CmAwTKQf3nyMTSTaxzAKcwgm670_01uk8NCV71FHD5-qhVOE1hguTh4qKNttFUyVqwv2XYnyYz59Ym_Syrma56vVFspX0N7zMvqwsKquUr-bAKSlo"
                alt="Dashboard Preview"
              />
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="px-4 md:px-16 py-24 md:py-32" id="features">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16 md:mb-24 space-y-6">
            <h2 className="text-4xl md:text-5xl font-semibold text-primary">Engineered for ease of life</h2>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {/* Feature 1: Screening */}
            <div className="group p-10 rounded-2xl bg-surface-container border border-outline-variant hover:border-primary transition-all duration-500">
              <div className="w-12 h-12 rounded-lg bg-surface-container-highest flex items-center justify-center mb-8 border border-outline-variant group-hover:scale-110 transition-transform">
                <span className="text-primary text-2xl">🔍</span>
              </div>
              <h3 className="text-xl md:text-2xl font-medium text-primary mb-4">Stock Search</h3>
              <p className="text-sm md:text-base text-on-surface-variant leading-relaxed">
                Search for stocks.
              </p>
            </div>
            {/* Feature 2: Sharing Searches */}
            <div className="group p-10 rounded-2xl bg-surface-container border border-outline-variant hover:border-primary transition-all duration-500">
              <div className="w-12 h-12 rounded-lg bg-surface-container-highest flex items-center justify-center mb-8 border border-outline-variant group-hover:scale-110 transition-transform">
                <img
                  src="https://lh3.googleusercontent.com/aida/ADBb0ugCoX60rbqop5bBcllgJiFFBFfHrx1qPDyBDZCyzvVCv4zVeoxZ3c3OmmOQb61vJLSchaHCvmKzdUhrETwZzQmF0ferz8iWOQeeFK8HgygnYauJVnPtMebU5pJXFLyb2FtU0Xi2CTcM1Dgi7GrOEvEnMbo_4i6HhOytIOkREid-O5eP7nwtLgqVb-6zEo9nUyOpHzonmuE86XRtcvhw2uTWhXVgGNl1nPsstLfQMKu4wJ9ly4XN8q972-w"
                  alt="Stock Screening Icon"
                  className="w-6 h-6 object-contain grayscale brightness-200"
                />
              </div>
              <h3 className="text-xl md:text-2xl font-medium text-primary mb-4">Stock Screening</h3>
              <p className="text-sm md:text-base text-on-surface-variant leading-relaxed">
                Screen your stocks based on many parameters
              </p>
            </div>
            {/* Feature 3: AI Analysis */}
            <div className="group p-10 rounded-2xl bg-surface-container border border-outline-variant hover:border-primary transition-all duration-500">
              <div className="w-12 h-12 rounded-lg bg-surface-container-highest flex items-center justify-center mb-8 border border-outline-variant group-hover:scale-110 transition-transform">
                <span className="text-primary text-2xl">✨</span>
              </div>
              <h3 className="text-xl md:text-2xl font-medium text-primary mb-4">Ai features</h3>
              <p className="text-sm md:text-base text-on-surface-variant leading-relaxed">
                Deep-dive intelligence on any subject. Our AI parses filings, transcripts, and sentiment automatically.
              </p>
            </div>
          </div>
        </div>
      </section>

      <Footer />
    </main>
  );
}