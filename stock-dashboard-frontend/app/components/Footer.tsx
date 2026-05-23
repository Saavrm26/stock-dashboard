import Link from "next/link";

export default function Footer() {
  return (
    <footer className="w-full py-16 px-4 md:px-16 bg-surface-container-lowest border-t border-outline-variant">
      <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-start gap-12">
        <div className="space-y-6">
          <span className="text-2xl font-black tracking-tighter text-primary">STOCK DASHBOARD</span>
          <p className="text-sm md:text-base text-on-surface-variant max-w-xs leading-relaxed">
            A passion project of Saarthak
          </p>
        </div>
        <div className="grid grid-cols-2 gap-x-16 gap-y-4">
          <Link className="text-on-surface-variant hover:text-primary transition-colors text-sm md:text-base" href="#">
            License
          </Link>
          <Link className="text-on-surface-variant hover:text-primary transition-colors text-sm md:text-base" href="#">
            Data Sources
          </Link>
          <Link className="text-on-surface-variant hover:text-primary transition-colors text-sm md:text-base" href="#">
            Compliance
          </Link>
        </div>
      </div>
    </footer>
  );
}