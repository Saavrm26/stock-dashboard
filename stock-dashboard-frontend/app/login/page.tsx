export default function LoginPage() {
  // TODO: remove and set a secure origin
  const apiOrigin =
    process.env.NEXT_PUBLIC_API_ORIGIN ?? "http://localhost:8080";
  const signInHref = new URL(
    "/oauth2/authorization/cognito",
    apiOrigin,
  ).href;

  return (
    <main className="flex min-h-screen flex-col items-center justify-center bg-zinc-50 px-6 font-sans dark:bg-black">
      <div className="flex w-full max-w-sm flex-col items-center gap-8 text-center">
        <h1 className="text-3xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Sign in
        </h1>
        <p className="text-sm text-zinc-600 dark:text-zinc-400">
          Continue to your account using the secure sign-in service.
        </p>
        <a
          href={signInHref}
          className="inline-flex w-full items-center justify-center rounded-lg bg-zinc-900 px-4 py-3 text-sm font-medium text-zinc-50 shadow-sm transition-colors hover:bg-zinc-800 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-zinc-900 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200 dark:focus-visible:outline-zinc-100"
        >
          Sign in
        </a>
      </div>
    </main>
  );
}
