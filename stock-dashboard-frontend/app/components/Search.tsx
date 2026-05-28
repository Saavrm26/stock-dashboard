"use client";

import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import { useDebounce } from "use-debounce";

const SEARCH_BASE_PATH = "/ticker/search";

export function Search() {
  const pathName = usePathname();
  const searchParams = useSearchParams();
  const router = useRouter();
  const initialQueryState = pathName === "/ticker/search" ? searchParams.get("q") || "" : "";
  const [searchQuery, setSearchQuery] = useState(initialQueryState);
  const [debouncedSearchQuery] = useDebounce(searchQuery, 500);

  const handleSearch: React.ChangeEventHandler<HTMLInputElement, HTMLInputElement> = (e) => {
    e.preventDefault();
    setSearchQuery(e.target.value)
  }

  useEffect(() => {
    const query = searchQuery.trim();
    if (debouncedSearchQuery && query !== "") {
      let newPath = SEARCH_BASE_PATH
      newPath += `?q=${encodeURIComponent(searchQuery)}`
      router.push(newPath)
    }
  }, [debouncedSearchQuery, router, searchQuery])

  return (
    <div className="relative">
      <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-[18px]">
        search
      </span>
      <input
        type="text"
        value={searchQuery}
        onChange={handleSearch}
        placeholder="Search for a ticker..."
        className="bg-gray-900 border border-gray-700 text-sm px-10 py-1.5 w-64 focus:outline-none focus:border-white transition-all rounded-sm text-white placeholder-gray-500"
      />
    </div>
  );
}