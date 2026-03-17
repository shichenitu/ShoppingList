import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

// CORS headers for browser compatibility
const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers":
    "authorization, x-client-info, apikey, content-type, x-custom-app-secret",
};

// Formatting logic to match your Android Repository
const toTitleCase = (str: string): string => {
  return str
    .trim()
    .split(/\s+/)
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
    .join(" ");
};

// Converts "dd.MM.yyyy" to "YYYY-MM-DD" for Postgres date type
const parseDeadline = (dateStr: string | undefined): string | null => {
  if (!dateStr || dateStr.trim() === "") return null;
  const parts = dateStr.split(".");
  if (parts.length === 3) {
    const day = parts[0].padStart(2, "0");
    const month = parts[1].padStart(2, "0");
    const year = parts[2];
    const isoDate = `${year}-${month}-${day}`;
    const d = new Date(isoDate);
    return isNaN(d.getTime()) ? null : isoDate;
  }
  return null;
};

Deno.serve(async (req) => {
  // Handle CORS preflight
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  // 1. GATEKEEPER: Verify the static API key from the header
  const clientSecret = req.headers.get("x-custom-app-secret");
  const appSecret = Deno.env.get("GARBAGE_APP_KEY");

  if (clientSecret !== appSecret) {
    return new Response(
      JSON.stringify({ error: "Unauthorized: Missing or invalid App Secret" }),
      {
        status: 401,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      },
    );
  }

  // 2. INITIALIZE: Use Service Role Key to bypass RLS
  const supabase = createClient(
    Deno.env.get("SUPABASE_URL") ?? "",
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? "",
  );

  const url = new URL(req.url);
  const params = Object.fromEntries(url.searchParams.entries());
  const { op, id, what, where, description, deadline } = params;

  try {
    let result;

    // READ: Get all items (No 'who' filter needed anymore)
    if (!op) {
      const { data, error } = await supabase
        .from("items")
        .select("*")
        .order("where", { ascending: true })
        .order("what", { ascending: true });
      if (error) throw error;
      result = data;
    }

    // INSERT: ?op=insert&what=tea&where=Irma&id=OPTIONAL_UUID
    else if (op === "insert" && what && where) {
      const newItem: any = {
        what: toTitleCase(what),
        where: toTitleCase(where),
        description: description || "",
        deadline: parseDeadline(deadline),
      };
      if (id) newItem.id = id;

      const { data, error } = await supabase
        .from("items")
        .insert([newItem])
        .select();
      if (error) throw error;
      result = { message: "Inserted", data };
    }

    // UPDATE: ?op=update&id=...&what=Coffee
    else if (op === "update" && id) {
      const updateData: any = {};
      if (what) updateData.what = toTitleCase(what);
      if (where) updateData.where = toTitleCase(where);
      if (description !== undefined) updateData.description = description;
      if (deadline !== undefined) updateData.deadline = parseDeadline(deadline);

      const { data, error } = await supabase
        .from("items")
        .update(updateData)
        .eq("id", id)
        .select();
      if (error) throw error;
      result = { message: "Updated", data };
    }

    // REMOVE: ?op=remove&id=...
    else if (op === "remove" && id) {
      const { error } = await supabase.from("items").delete().eq("id", id);
      if (error) throw error;
      result = { message: "Removed" };
    } else {
      return new Response(JSON.stringify({ error: "Invalid parameters" }), {
        status: 400,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    return new Response(JSON.stringify(result), {
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });
  } catch (err) {
    return new Response(JSON.stringify({ error: err.message }), {
      status: 500,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });
  }
});
