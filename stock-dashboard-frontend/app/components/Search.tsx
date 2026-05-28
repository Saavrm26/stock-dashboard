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

  return <div>
    <input
      type="text"
      value={searchQuery}
      onChange={handleSearch}
      placeholder="Search for a ticker..."
      className="border p-2 rounded w-full" />
  </div>
}